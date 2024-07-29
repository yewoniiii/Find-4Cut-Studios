package com.eureka.findselfie.mymember;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class MymemberController {

	@Autowired
	private MymemberRepository mymemRepo;
	
	// 로그인 기능
	@PostMapping("/member/login")
	@ResponseBody
	public Map<String, Object> login(
			@RequestParam("mid") String mid,
			@RequestParam("mpwd") String mpwd,
			HttpSession session) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("code", "error");
		
		Optional<Mymember> mem = mymemRepo.findById(mid);
		if (mem.isPresent()) {
			Mymember mymember = mem.get();
			if (mymember.getMpwd().equals(mpwd)) {
				result.put("code", "ok");
				result.put("message", "로그인완료");
				
				/* HttpSession은 접속하는 브라우저만의 공간을 의미 
				 * 로그인에 성공하면 브라우저만의 공간인 session에 "member"라는 키와
				 * 로그인 사용자 정보를 담은 Mymember 객체를 저장한다.
				 * 로그인 여부 확인하고자 할 때 session에 "member" 키가 저장되어 있는지 확인하면 됨
				 */
				session.setAttribute("member", mymember);
			} else if (mymember.getMid().equals(mid)){
				result.put("message", "비밀번호가 일치하지 않습니다.");
			}
		} else {
			result.put("message", "ID가 존재하지 않습니다.");
		}
		System.out.println(result);
		return result;
	}
	
	// 로그아웃 기능 
	@GetMapping("/member/logout")
	@ResponseBody
	public Map<String, Object> logout(HttpSession session) {
		HashMap<String, Object> result = new HashMap<>();
		
		Object object = session.getAttribute("member");
		String message = "";
		if (object == null) {
			message += "로그인 안 된 상태";
		} else {
			message += "로그인 된 상태";
		}
		
		/* invalidate()는 브라우저만의 정보를 담은 session 공단을 지우고 다시 만듦
		 * 이로써 session에 저장했던 모든 정보를 없애버리니 로그인 안 된 상태로 바꾸는 것
		 */
		session.invalidate(); // 로그인 정보 삭제
		result.put("code", "ok");
		result.put("message", message + "로그아웃 완료");
		
		System.out.println(result);
		return result;
	}
	
	// 로그인 체크
	@GetMapping("/member/check_login")
	@ResponseBody
	public Map<String, Object> check_login(HttpSession session) {
		HashMap<String, Object> result = new HashMap<>();
		
		Object object = session.getAttribute("member");
		
		// 브라우저만의 공간인 session에 "member"로 저장된 게 있으면 로그인한 것이고 없으면 로그인 하지 않은 것
		if (object == null) {
			result.put("code", "error");
			result.put("message", "Not Login");
		} else {
			result.put("code", "ok");
			result.put("message", "On Login");
			Mymember mm = (Mymember)object;
			mm.setMpwd(null);
			result.put("data", mm); // 로그인된 사용자 정보
		}
		
		System.out.println(result);
		return result;
	}
	
	@PostMapping("/member/regist")
    @ResponseBody
    public Map<String, Object> regist(@RequestBody Mymember mm) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (mymemRepo.existsById(mm.getMid())) {
                result.put("code", "duplicate");
                result.put("message", "이미 존재하는 ID입니다.");
            } else {
                mymemRepo.save(mm);
                result.put("code", "ok");
            }
        } catch (DataIntegrityViolationException e) {
            result.put("code", "error");
            result.put("message", "회원가입 중 오류가 발생했습니다.");
        } catch (Exception e) {
            result.put("code", "error");
            result.put("message", "회원가입 중 오류가 발생했습니다.");
        }
        return result;
    }
}
