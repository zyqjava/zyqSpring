package com.zyqSpring.mvc.support;


public class ProtocolFactory {

    public static Protocol getProtocol() {
        String name = System.getProperty("protocolName");
        if (name == null || name.equals("")) name = "http";
        switch (name) {
            case "http":
                return new HttpProtocol();
            case "netty":
                return new HttpProtocol();
            default:
                break;
        }
        return new HttpProtocol();
    }
}
