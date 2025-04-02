//package com.pn.career.repositories;
//
//import com.pn.career.models.ChatHistory;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.List;
//public interface ChatHistoryRepository extends CrudRepository<ChatHistory, String> {
//    List<ChatHistory> findBySessionIdOrderByTimestampDesc(String sessionId);
//    List<ChatHistory> findByUserIdOrderByTimestampDesc(String userId);
//    void deleteBySessionId(String sessionId);
//}
