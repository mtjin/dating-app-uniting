package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CommunityPost  implements Serializable, Comparable<CommunityPost> {
    private String postId; //커뮤니티글 고유토큰
    private String writerNickName; //작성자
    private String writerUid; //작성자 고유토큰
    private String writerGender; //작성자 성별
    private String createDate; //작성 시간
    private long createTimestamp; //작성 시간 millisecond
    private String title; //제목
    private String content; //내용
    private String photoUrl; //사진url
    private String board; //게시판 종류 (전체 게시판인지, 대학별 게시판인지)
    private List<String> likeList; //좋아요누른 사람 리스트
    private int like; //좋아요 갯수
    private HashMap<String, String> nicknameList; //댓글 작성자의 uid/닉네임 매칭 리스
    private List<String> commentWriterUidList; //댓글 작성 리스트
    private List<String> commentList; //댓글 리스트
    private boolean deleted; // 작성자가 삭제했다는 글 보이게함(삭제도 하지 않고 DB에도 존재)
    private boolean expired; // 완전 삭제(DB에는 존재)

    public CommunityPost() {
    }

    public CommunityPost(String postId, String writerNickName, String writerUid, String writerGender, String createDate, long createTimestamp, String title, String content, String photoUrl, String board, List<String> likeList, int like, HashMap<String, String> nicknameList, List<String> commentWriterUidList, List<String> commentList, boolean deleted, boolean expired) {
        this.postId = postId;
        this.writerNickName = writerNickName;
        this.writerUid = writerUid;
        this.writerGender = writerGender;
        this.createDate = createDate;
        this.createTimestamp = createTimestamp;
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
        this.board = board;
        this.likeList = likeList;
        this.like = like;
        this.nicknameList = nicknameList;
        this.commentWriterUidList = commentWriterUidList;
        this.commentList = commentList;
        this.deleted = deleted;
        this.expired = expired;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getWriterNickName() {
        return writerNickName;
    }

    public void setWriterNickName(String writerNickName) {
        this.writerNickName = writerNickName;
    }

    public String getWriterUid() {
        return writerUid;
    }

    public void setWriterUid(String writerUid) {
        this.writerUid = writerUid;
    }

    public String getWriterGender() {
        return writerGender;
    }

    public void setWriterGender(String writerGender) {
        this.writerGender = writerGender;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
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

    public HashMap<String, String> getNicknameList() {
        return nicknameList;
    }

    public void setNicknameList(HashMap<String, String> nicknameList) {
        this.nicknameList = nicknameList;
    }

    public List<String> getCommentWriterUidList() {
        return commentWriterUidList;
    }

    public void setCommentWriterUidList(List<String> commentWriterUidList) {
        this.commentWriterUidList = commentWriterUidList;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<String> commentList) {
        this.commentList = commentList;
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

    @Override
    public int compareTo(CommunityPost o) {
        return this.postId.compareTo(o.getPostId());
    }


}
