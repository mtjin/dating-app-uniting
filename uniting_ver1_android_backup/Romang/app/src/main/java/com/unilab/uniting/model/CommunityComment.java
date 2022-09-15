package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.List;

public class CommunityComment implements Serializable {
    private String commentId; //댓글 고유토큰
    private String postId; // 댓글이 달린 원글 고유토큰
    private String postWriterUid; //원글 작성자의 uid
    private String commentWriterNickname; //댓글 작성자 닉네임
    private String commentWriterUid; //댓글 작성자 본인 고유토큰
    private String writerGender; //작성자 성별
    private String date; //작성 시간
    private long createTimestamp;
    private String content; //내용
    private List<String> likeList; //좋아요누른 사람들uid 리스트
    private int like; //좋아요 갯수
    private String replyCommentId; //대댓글 달려는 댓글의 id
    private String replyNickName; //대댓글 달려는 댓글쓴 사람의 닉네임
    private int flag; //otto라이브러리에서 사용할 플래그 ( 0 이면 대댓글, 1이면 신고하기클릭)
    private boolean deleted;
    private boolean expired;

    public CommunityComment() {
    }

    public CommunityComment(String commentId, String postId, String postWriterUid, String commentWriterNickname, String commentWriterUid, String writerGender, String date, long createTimestamp, String content, List<String> likeList, int like, String replyCommentId, String replyNickName, int flag, boolean deleted, boolean expired) {
        this.commentId = commentId;
        this.postId = postId;
        this.postWriterUid = postWriterUid;
        this.commentWriterNickname = commentWriterNickname;
        this.commentWriterUid = commentWriterUid;
        this.writerGender = writerGender;
        this.date = date;
        this.createTimestamp = createTimestamp;
        this.content = content;
        this.likeList = likeList;
        this.like = like;
        this.replyCommentId = replyCommentId;
        this.replyNickName = replyNickName;
        this.flag = flag;
        this.deleted = deleted;
        this.expired = expired;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostWriterUid() {
        return postWriterUid;
    }

    public void setPostWriterUid(String postWriterUid) {
        this.postWriterUid = postWriterUid;
    }

    public String getCommentWriterNickname() {
        return commentWriterNickname;
    }

    public void setCommentWriterNickname(String commentWriterNickname) {
        this.commentWriterNickname = commentWriterNickname;
    }

    public String getCommentWriterUid() {
        return commentWriterUid;
    }

    public void setCommentWriterUid(String commentWriterUid) {
        this.commentWriterUid = commentWriterUid;
    }

    public String getWriterGender() {
        return writerGender;
    }

    public void setWriterGender(String writerGender) {
        this.writerGender = writerGender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<String> likeList) {
        this.likeList = likeList;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getReplyNickName() {
        return replyNickName;
    }

    public void setReplyNickName(String replyNickName) {
        this.replyNickName = replyNickName;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
