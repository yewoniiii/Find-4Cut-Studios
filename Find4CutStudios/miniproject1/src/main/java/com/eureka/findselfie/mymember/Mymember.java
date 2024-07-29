package com.eureka.findselfie.mymember;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Mymember {
	@Id
	private String mid;
	private String mname, mpwd, mtel, memail;
}
