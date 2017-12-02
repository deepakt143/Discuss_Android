package com.discuss.ui.comment.view.impl;

import com.discuss.data.CommentRepository;
import com.discuss.datatypes.Comment;
import com.discuss.ui.CommentSummary;
import com.discuss.ui.comment.view.UserCommentPresenter;
import com.discuss.utils.Utils;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 *
 * @author Deepak Thakur
 */
public class UserCommentPresenterImpl implements UserCommentPresenter {
    private int questionID;
    private CommentRepository commentRepository;
    private Single<Comment> comment;
    private volatile String editedComment;
    private volatile String initialComment;

    @Inject
    public UserCommentPresenterImpl(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    @Override
    public Observable<CommentSummary> init(int questionID) {
        this.questionID = questionID;
        this.comment = commentRepository.userAddedComment(questionID);
        return comment.map(commentCommentSummaryFunc1).doOnSuccess(commentSummary -> {
            if (commentSummary != null) {
                editedComment = initialComment = commentSummary.getText();
            }
        }).toObservable();
    }

    public void setEditedComment(final String editedComment) {
        this.editedComment = editedComment;
    }

    public String getEditedComment() {
        return editedComment;
    }

    @Override
    public void save() {
        int commentID = comment.toBlocking().value().getCommentId();
        if(!Utils.isEqual(initialComment, editedComment)) {
            commentRepository.updateCommentText(commentID, editedComment);
        }
        commentRepository.save();
        this.comment = commentRepository.userAddedComment(questionID);
    }


    private Func1<Comment, CommentSummary> commentCommentSummaryFunc1 = userComment -> {
        if(null == userComment) {
            return null;
        }
        return CommentSummary.builder()
                .commentId(userComment.getCommentId())
                .imageUrl(userComment.getImageUrl())
                .liked(userComment.isLiked())
                .text(userComment.getText())
                .personId(userComment.getPersonId())
                .personName(userComment.getPersonName())
                .views(userComment.getViews())
                .likes(userComment.getLikes())
                .build();
    };
}