package com.adroitandroid.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pv on 29/10/16.
 */
public class UserVotesForSnippets implements Serializable {
    private Long userId;
    private List<StorySnippetVote> votes;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<StorySnippetVote> getVotes() {
        return votes;
    }

    public void setVotes(List<StorySnippetVote> votes) {
        this.votes = votes;
    }

    public static class StorySnippetVote implements Serializable {
        private Integer voteValue;
        private Long snippetId;

        public Integer getVoteValue() {
            return voteValue;
        }

        public void setVoteValue(Integer voteValue) {
            this.voteValue = voteValue;
        }

        public Long getSnippetId() {
            return snippetId;
        }

        public void setSnippetId(Long snippetId) {
            this.snippetId = snippetId;
        }
    }
}
