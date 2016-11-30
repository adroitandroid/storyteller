package com.adroitandroid.controller;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.ChapterApproval;
import com.adroitandroid.model.ChapterInput;
import com.adroitandroid.model.service.ChapterService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pv on 30/11/16.
 */
@RestController
@RequestMapping(value = "/chapter")
public class ChapterController extends AbstractController {
    @Autowired
    private ChapterService chapterService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonElement addNewChapter(@RequestBody ChapterInput chapterInput) {
        chapterService.validateAddChapterInput(chapterInput);
        Chapter chapter = chapterService.addChapter(chapterInput);
        return prepareResponseFrom(chapter);
    }

    @RequestMapping(value = "/approve", method = RequestMethod.PATCH)
    public void approveChapter(@RequestBody ChapterApproval chapterApproval) {
        chapterService.addChapterApproval(chapterApproval.chapterId, chapterApproval.notificationId, chapterApproval.approval);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
