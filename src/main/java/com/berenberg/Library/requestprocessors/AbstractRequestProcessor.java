package com.berenberg.Library.requestprocessors;

public abstract class AbstractRequestProcessor<req extends Object, res extends Object> {
    public res handleRequest(req request) {
        validateInput(request);
        return processRequest(request);
    }

    public void validateInput(req request) {
    }


    public abstract res processRequest(req request);
}
