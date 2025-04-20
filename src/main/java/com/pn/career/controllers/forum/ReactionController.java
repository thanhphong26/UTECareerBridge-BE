package com.pn.career.controllers.forum;
import com.pn.career.services.forum.IReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final IReactionService reactionService;


}
