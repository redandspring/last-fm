package ru.redandspring.services;

public class ServiceException extends Exception {

    private static final String BLANK_USER_MESSAGE = "Empty message";
    private final String userMessage;

    public ServiceException(final String userMessage) {
        this(userMessage, null);
    }

    public ServiceException(final Throwable cause) {
        this(BLANK_USER_MESSAGE, cause);
    }

    public ServiceException(final String userMessage, final Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "userMessage='" + userMessage + '\'' +
                "} " + super.toString();
    }
}
