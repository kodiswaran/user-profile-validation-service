package com.intuit.userprofile.helper;

import java.util.Map;

public interface IApiRequestHelper {

    public <Req, Res> Res post( String url, Map<String, String> header, Req body, Class<Res> response);
}
