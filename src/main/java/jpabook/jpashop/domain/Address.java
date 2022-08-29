package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // jpa의 내장 타입. 어딘가에 내장될 수 있다
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() { // 기본 생성자. 함부로 생성 못하도록
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}