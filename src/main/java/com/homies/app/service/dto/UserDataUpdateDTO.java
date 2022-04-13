package com.homies.app.service.dto;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Arrays;

public class UserDataUpdateDTO {

    @Id
    private Long id;

    @Lob
    private byte[] photo;

    private String photoContentType;

    @Size(min = 6, max = 50)
    private String phone;

    private LocalDate birthDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UserDataUpdateDTO{" +
            "photo=" + Arrays.toString(photo) +
            ", photoContentType='" + photoContentType + '\'' +
            ", phone='" + phone + '\'' +
            ", birthDate=" + birthDate +
            '}';
    }
}
