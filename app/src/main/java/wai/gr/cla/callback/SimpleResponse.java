package wai.gr.cla.callback;

import java.io.Serializable;

import wai.gr.cla.model.LzyResponse;

/**
 * Created by Finder丶畅畅 on 2017/5/13 00:12
 * QQ群481606175
 */

public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public int code;
    public String msg;

    public LzyResponse toLzyResponse() {
        LzyResponse lzyResponse = new LzyResponse();
        lzyResponse.setCode(code);
        lzyResponse.setMsg(msg);
        return lzyResponse;
    }
}
