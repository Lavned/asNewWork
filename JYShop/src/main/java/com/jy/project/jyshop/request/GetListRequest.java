package com.jy.project.jyshop.request;

import com.jy.project.jyshop.base.BaseRequest;

import org.xutils.http.annotation.HttpResponse;

/**
 * Created by mango on 2017/11/17.
 */

public class GetListRequest extends BaseRequest {

    public String userid;
    public int pageindex;
    public int pagesize;

    public String kword;
    public String status;
    public String categoryid;
}
