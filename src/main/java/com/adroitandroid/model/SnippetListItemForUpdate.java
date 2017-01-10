package com.adroitandroid.model;

/**
 * Created by pv on 05/01/17.
 */

import com.adroitandroid.serializer.OptionalInGson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Response type, not entity
 */
public class SnippetListItemForUpdate extends SnippetListItem {
    public static final String UPDATES_LIST_IN_SNIPPET_LIST_ITEM_FOR_UPDATE = "updates_list_in_SnippetListItemForUpdate";
    private Date lastUpdateAt;

    @OptionalInGson(exclude = UPDATES_LIST_IN_SNIPPET_LIST_ITEM_FOR_UPDATE)
    private List<String> updateList = new ArrayList<>();

//    Called from query
    public SnippetListItemForUpdate(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser,
                                    Timestamp lastUpdateTime) {
        super(snippet, parentSnippet, snippetStats, authorUser);
        this.lastUpdateAt = lastUpdateTime;
    }

    public SnippetListItemForUpdate(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser,
                                    Date lastUpdateTime, boolean isContributionOfFollowed) {
        super(snippet, parentSnippet, snippetStats, authorUser);
        this.lastUpdateAt = lastUpdateTime;
        setCategoryText();
    }

    public SnippetListItemForUpdate() {
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    private void setCategoryText() {
        if (isStoryStarter()) {
            setCategory("new story from people you follow");
        } else {
//            TODO: check if end and set category accordingly
            setCategory("new snippet from people you follow");
        }
    }

    public void setCategoryFromUpdate() {
        int updateCount = updateList.size();
        if (updateCount == 1) {
            setCategory(updateList.get(0));
        } else if (updateCount == 2) {
            setCategory(updateList.get(0) + " and " + updateList.get(1));
        } else if (updateCount == 3) { //when votes, snippets and ends are there on top of interesting snippet
            setCategory(updateList.get(0) + ", " + updateList.get(1) + " and " + updateList.get(2));
        }
    }

    public SnippetListItemForUpdate setVotes(VoteUpdate voteUpdate) {
        basicInit(voteUpdate.getSnippet(), voteUpdate.getParentSnippet(), voteUpdate.getSnippetStats(), voteUpdate.getUser());
        if (this.lastUpdateAt == null || this.lastUpdateAt.before(voteUpdate.getMostRecentUpdateTime())) {
            this.lastUpdateAt = voteUpdate.getMostRecentUpdateTime();
        }

        long deltaVoteSum = voteUpdate.getVoteSum();
        if (deltaVoteSum != 0) {
            String deltaVoteSumText = (deltaVoteSum > 0 ? "+" + deltaVoteSum : String.valueOf(deltaVoteSum))
                    + (Math.abs(deltaVoteSum) == 1 ? " vote" : " votes");
            updateList.add(deltaVoteSumText);
        }
        return this;
    }

    public SnippetListItemForUpdate setChildInfo(ChildUpdate childUpdate) {
        basicInit(childUpdate.getSnippet(), childUpdate.getParentSnippet(), childUpdate.getSnippetStats(), childUpdate.getUser());
        if (this.lastUpdateAt == null || this.lastUpdateAt.before(childUpdate.getMostRecentUpdateTime())) {
            this.lastUpdateAt = childUpdate.getMostRecentUpdateTime();
        }

        if (childUpdate.getNumChildren() > 0) {
            String deltaSnippetsText = childUpdate.getNumChildren() + " new" + (childUpdate.getNumChildren() > 1 ? " snippets" : " snippet");
            updateList.add(deltaSnippetsText);
        }
        return this;
    }
}
