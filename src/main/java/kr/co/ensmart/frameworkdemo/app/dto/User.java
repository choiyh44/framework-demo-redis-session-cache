package kr.co.ensmart.frameworkdemo.app.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {
	private int id;
	private String userName;
	private String password;
	private String email;

}
