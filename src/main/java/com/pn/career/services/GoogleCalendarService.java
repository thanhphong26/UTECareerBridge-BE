package com.pn.career.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.EntryPoint;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.exceptions.AuthenticationException;
import com.pn.career.models.EmployerCredential;
import com.pn.career.repositories.EmployerCredentialRepository;
import com.pn.career.responses.MeetingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Interview Scheduler";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar");
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private final CredentialService credentialService;
    private  final EmployerCredentialRepository credentialRepository;
    @Value("classpath:calendar.json")
    private Resource credentialsFile;

    @Value("${google.calendar.timezone}")
    private String calendarTimezone;
    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    public boolean isEmployerGoogleAuthenticated(Integer employerId) {
        Optional<EmployerCredential> credentialOpt = credentialService.findByEmployerId(employerId);
        return credentialOpt.isPresent() &&
                credentialOpt.get().isGoogleAuthValid() &&
                credentialOpt.get().getGoogleAccessToken() != null &&
                credentialOpt.get().getGoogleRefreshToken() != null;
    }
    private Credential createCredentialFromTokens(String accessToken, String refreshToken) {
        return new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(googleClientId, googleClientSecret)
                .build()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
    }

    /**
     * Làm mới token khi hết hạn
     */
    private String refreshGoogleToken(Integer employerId, String refreshToken) {
        try {
            Credential credential = createCredentialFromTokens(null, refreshToken);

            // Try to refresh token
            boolean refreshed = credential.refreshToken();
            if (refreshed) {
                String newAccessToken = credential.getAccessToken();

                // Save new token to database
                credentialService.updateGoogleToken(employerId, newAccessToken);
                return newAccessToken;
            } else {
                log.error("Failed to refresh Google token");
                credentialService.markCredentialsInvalid(employerId);

                throw new AuthenticationException("Google Calendar authorization expired. Please reauthorize.");
            }
        } catch (IOException e) {
            // Mark credentials as invalid in database to prompt re-authorization
            credentialService.markCredentialsInvalid(employerId);

            throw new AuthenticationException("Google Calendar authorization error. Please reauthorize.", e);
        }
    }

    private Calendar getCalendarServiceForEmployer(Integer employerId)
            throws IOException, GeneralSecurityException {
        Optional<EmployerCredential> credentialOpt = credentialService.findByEmployerId(employerId);
        if (credentialOpt.isPresent() &&
                credentialOpt.get().isGoogleAuthValid() &&
                credentialOpt.get().getGoogleAccessToken() != null &&
                credentialOpt.get().getGoogleRefreshToken() != null) {

            EmployerCredential empCredential = credentialOpt.get();
            String accessToken = empCredential.getGoogleAccessToken();
            String refreshToken = empCredential.getGoogleRefreshToken();
            // Create credential from saved tokens
            Credential credential = createCredentialFromTokens(accessToken, refreshToken);

            // Check if token is about to expire
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
                // Token about to expire, refresh it
                accessToken = refreshGoogleToken(employerId, refreshToken);
                if (accessToken == null) {
                    throw new AuthenticationException("Unable to refresh Google token");
                }
                credential.setAccessToken(accessToken);
            }

            // Create service with employer's credential
            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } else {
            // Check if credentials exist but are invalid
            if (credentialOpt.isPresent() && !credentialOpt.get().isGoogleAuthValid()) {
                throw new AuthenticationException("Google Calendar authorization expired. Please reauthorize.");
            }
            // If no valid credentials, throw exception
            throw new AuthenticationException("Google Calendar authorization not found. Please authorize.");
        }
    }

    private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Tải thông tin client secret
        Resource resource = new ClassPathResource("calendar.json");
        if (!resource.exists()) {
            throw new FileNotFoundException("Resource không tìm thấy: " + resource.getFilename());
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(resource.getInputStream()));

        // Xây dựng flow cho authorization và yêu cầu người dùng ủy quyền
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Khởi tạo Google Calendar service
     * @return Calendar service instance
     * @throws IOException
     * @throws GeneralSecurityException
     */
//    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
//        log.info("Khởi tạo Google Calendar service");
//        try {
//            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            log.info("HTTP Transport: {}", httpTransport);
//
//            Credential credential = getCredentials(httpTransport);
//            log.info("Credential: {}", credential);
//
//            // Cập nhật cách tạo Calendar service với API mới
//            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//
//            log.info("Calendar Service successfully created");
//            return service;
//        } catch (Exception e) {
//            log.error("Error creating Calendar service", e);
//            throw e;
//        }
//    }
    public String createCalendarEventAsEmployer(
            InterviewRequestDTO interviewRequestDTO,
            MeetingResponse meetingResponse,
            Integer employerId) throws IOException, GeneralSecurityException {
        // Lấy Calendar service cho nhà tuyển dụng
        Calendar service = getCalendarServiceForEmployer(employerId);

        // Tạo sự kiện như bình thường
        DateTime startDateTime = new DateTime(
                interviewRequestDTO.getStartTime()
                        .atZone(ZoneId.of(calendarTimezone))
                        .toInstant().toEpochMilli());

        DateTime endDateTime = new DateTime(
                interviewRequestDTO.getStartTime()
                        .plusMinutes(interviewRequestDTO.getDurationMinutes())
                        .atZone(ZoneId.of(calendarTimezone))
                        .toInstant().toEpochMilli());

        // Tạo danh sách người tham gia
        EventAttendee[] attendeesArray = new EventAttendee[interviewRequestDTO.getAttendeeEmails().size() + 1];

        // Thêm ứng viên
        attendeesArray[0] = new EventAttendee()
                .setEmail(interviewRequestDTO.getCandidateEmail())
                .setDisplayName("Ứng viên");

        // Thêm các người tham gia khác
        for (int i = 0; i < interviewRequestDTO.getAttendeeEmails().size(); i++) {
            attendeesArray[i + 1] = new EventAttendee()
                    .setEmail(interviewRequestDTO.getAttendeeEmails().get(i));
        }
        // Tạo nội dung sự kiện
        Event event = new Event()
                .setSummary(interviewRequestDTO.getTitle())
                .setLocation("Phỏng vấn trực tuyến")
                .setDescription(String.format("%s\n\nLink phỏng vấn: %s",
                        interviewRequestDTO.getDescription(),
                        meetingResponse.getJoinUrl()))
                .setStart(new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone(calendarTimezone))
                .setEnd(new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone(calendarTimezone))
                .setAttendees(Arrays.asList(attendeesArray))
                .setConferenceData(new ConferenceData()
                        .setEntryPoints(Collections.singletonList(
                                new EntryPoint()
                                        .setEntryPointType("video")
                                        .setUri(meetingResponse.getJoinUrl())
                                        .setLabel("Tham gia phỏng vấn")
                        )))
                .setReminders(new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(
                                new EventReminder().setMethod("email").setMinutes(24 * 60),  // 1 ngày trước
                                new EventReminder().setMethod("popup").setMinutes(30)        // 30 phút trước
                        )));

        // Lấy email nhà tuyển dụng (tùy chọn)
        Optional<EmployerCredential> credentialOpt = credentialService.findByEmployerId(employerId);
        if (credentialOpt.isPresent() && credentialOpt.get().getEmployer() != null) {
            String organizerEmail = credentialOpt.get().getEmployer().getEmail();
            // Đặt người tổ chức là nhà tuyển dụng
            event.setOrganizer(new Event.Organizer()
                    .setEmail(organizerEmail)
                    .setDisplayName("Nhà tuyển dụng")
                    .setSelf(true));
        }
        // Chèn sự kiện
        event = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .setSendNotifications(true)
                .execute();
        return event.getId();
    }
    /**
     * Tạo sự kiện trên Google Calendar cho cuộc phỏng vấn
     * @param interviewRequestDTO thông tin yêu cầu phỏng vấn
     * @param meetingResponse thông tin cuộc họp Zoom đã tạo
     * @return ID của sự kiện Google Calendar đã tạo
     * @throws IOException
     * @throws GeneralSecurityException
     */
//    public String createCalendarEvent(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse)
//            throws IOException, GeneralSecurityException {
//
//        // Xây dựng service
//        Calendar service = getCalendarService();
//        log.info("Service: {}", service);
//
//        // Chuyển đổi thời gian
//        DateTime startDateTime = new DateTime(
//                interviewRequestDTO.getStartTime()
//                        .atZone(ZoneId.of(calendarTimezone))
//                        .toInstant().toEpochMilli());
//
//        DateTime endDateTime = new DateTime(
//                interviewRequestDTO.getStartTime()
//                        .plusMinutes(interviewRequestDTO.getDurationMinutes())
//                        .atZone(ZoneId.of(calendarTimezone))
//                        .toInstant().toEpochMilli());
//
//        // Tạo danh sách người tham gia
//        EventAttendee[] attendeesArray = new EventAttendee[interviewRequestDTO.getAttendeeEmails().size() + 1];
//
//        // Thêm ứng viên
//        attendeesArray[0] = new EventAttendee()
//                .setEmail(interviewRequestDTO.getCandidateEmail())
//                .setDisplayName("Ứng viên");
//
//        // Thêm các người tham gia khác
//        for (int i = 0; i < interviewRequestDTO.getAttendeeEmails().size(); i++) {
//            attendeesArray[i + 1] = new EventAttendee()
//                    .setEmail(interviewRequestDTO.getAttendeeEmails().get(i));
//        }
//
//        // Tạo nội dung sự kiện
//        Event event = new Event()
//                .setSummary(interviewRequestDTO.getTitle())
//                .setLocation("Phỏng vấn trực tuyến qua Zoom")
//                .setDescription(String.format("%s\n\nLink phỏng vấn: %s",
//                        interviewRequestDTO.getDescription(),
//                        meetingResponse.getJoinUrl()))
//                .setStart(new EventDateTime()
//                        .setDateTime(startDateTime)
//                        .setTimeZone(calendarTimezone))
//                .setEnd(new EventDateTime()
//                        .setDateTime(endDateTime)
//                        .setTimeZone(calendarTimezone))
//                .setAttendees(Arrays.asList(attendeesArray))
//                .setConferenceData(new ConferenceData()
//                        .setEntryPoints(Collections.singletonList(
//                                new EntryPoint()
//                                        .setEntryPointType("video")
//                                        .setUri(meetingResponse.getJoinUrl())
//                                        .setLabel("Tham gia phỏng vấn Zoom")
//                        )))
//                .setReminders(new Event.Reminders()
//                        .setUseDefault(false)
//                        .setOverrides(Arrays.asList(
//                                new EventReminder().setMethod("email").setMinutes(24 * 60),  // 1 ngày trước
//                                new EventReminder().setMethod("popup").setMinutes(30)        // 30 phút trước
//                        )));
//
//        // Chèn sự kiện với API mới
//        event = service.events().insert("primary", event)
//                .setConferenceDataVersion(1) // Hỗ trợ thông tin hội nghị (Zoom link)
//                .setSendNotifications(true) // Gửi thông báo tới người tham gia
//                .execute();
//
//        return event.getId();
//    }
}