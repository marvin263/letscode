package a.b.c;

import java.util.Objects;

public class ContentInfo {
    private int id;
    private String title;
    private int type;
    private int pv;
    private UserInfo userInfo;

    @Override
    public String toString() {
        return "ContentInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", pv=" + pv +
                ", userInfo=" + userInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentInfo that = (ContentInfo) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfo {
        private long userId;
        private String nickName;

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId=" + userId +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }
}
