package wai.gr.cla.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Finder丶畅畅 on 2018/4/29 22:06
 * QQ群481606175
 */

public class JJ {
    @SerializedName("138")
    private List<_$138Bean> _$138;

    public List<_$138Bean> get_$138() {
        return _$138;
    }

    public void set_$138(List<_$138Bean> _$138) {
        this._$138 = _$138;
    }

    public static class _$138Bean {
        ClassTag info;
        List<ZB> children;

        public ClassTag getInfo() {
            return info;
        }

        public void setInfo(ClassTag info) {
            this.info = info;
        }

        public List<ZB> getChildren() {
            return children;
        }

        public void setChildren(List<ZB> children) {
            this.children = children;
        }
    }
}
