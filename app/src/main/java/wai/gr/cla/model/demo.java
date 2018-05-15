package wai.gr.cla.model;

import java.util.List;

/**
 * Created by Finder丶畅畅 on 2017/6/13 21:29
 * QQ群481606175
 */

public class demo {

    /**
     * id : 30
     * uid : 1
     * teacher_id : 3
     * teacher_course_id : 3
     * question : ffffffffffffffffff
     * question_images : ["/uploads/answer/30/2017061317564314973478035799.jpg"]
     * answer :
     * answer_images : []
     * answer_date : 0000-00-00 00:00:00
     * answer_unread : 0
     * cdate : 2017-06-13 17:56:47
     * mdate : 2017-06-13 17:56:47
     */

    private int id;
    private int uid;
    private int teacher_id;
    private int teacher_course_id;
    private String question;
    private String answer;
    private String answer_date;
    private int answer_unread;
    private String cdate;
    private String mdate;
    private List<String> question_images;
    private List<?> answer_images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public int getTeacher_course_id() {
        return teacher_course_id;
    }

    public void setTeacher_course_id(int teacher_course_id) {
        this.teacher_course_id = teacher_course_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer_date() {
        return answer_date;
    }

    public void setAnswer_date(String answer_date) {
        this.answer_date = answer_date;
    }

    public int getAnswer_unread() {
        return answer_unread;
    }

    public void setAnswer_unread(int answer_unread) {
        this.answer_unread = answer_unread;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public List<String> getQuestion_images() {
        return question_images;
    }

    public void setQuestion_images(List<String> question_images) {
        this.question_images = question_images;
    }

    public List<?> getAnswer_images() {
        return answer_images;
    }

    public void setAnswer_images(List<?> answer_images) {
        this.answer_images = answer_images;
    }
}
