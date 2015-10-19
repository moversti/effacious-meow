package com.mycompany.domain;

public class Error {

    public static Error withCause(String cause) {
        Error e = new Error();
        e.setError(cause);
        return e;
    }
    private String error;

    @Override
    public String toString() {
        return "Error: "+getError();
    }
    
    /**
     * @return the error
     */
    public String getError(){
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}


