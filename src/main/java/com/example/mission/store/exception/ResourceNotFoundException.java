package com.example.mission.store.exception;

public class ResourceNotFoundException extends Exception  {
    public ResourceNotFoundException(String message) {
        super(message); // X 클래스의 생성자에 메시지를 전달합니다.
    }
}