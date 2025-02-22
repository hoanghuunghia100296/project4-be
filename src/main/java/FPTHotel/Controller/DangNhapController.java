package FPTHotel.Controller;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import FPTHotel.Dto.ChangePassDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import FPTHotel.Common.Common;
import FPTHotel.Model.Display;
import FPTHotel.Model.HistoryLogin;
import FPTHotel.Model.Account;
import FPTHotel.Services.GiaoDienService;
import FPTHotel.Services.ITaikhoanServices;
import FPTHotel.Services.LichSuDangNhapService;

@Controller
@Transactional
public class DangNhapController {

	@Autowired
	ITaikhoanServices dangnhapservice;

	@Autowired
	LichSuDangNhapService lichSuDangNhapService;

	@Autowired
	GiaoDienService giaoDienService;

	int checklogin = 0;

	@ModelAttribute(name = "changeURL")
	public String changeURL() {
		return "login";
	}

	@RequestMapping("/login")
	public String login(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.invalidate();
		if (checklogin == 1) {
			model.addAttribute("message", "Username or password is incorrect");
			checklogin = 0;
		}
		// get Organization Name
		session = request.getSession();
		Display giaoDien = new Display();
		giaoDien = ((List<Display>) giaoDienService.findAll()).get(0);
		session.setAttribute("tenToChuc", giaoDien.getTenToChuc());
		session.setAttribute("diaChi", giaoDien.getDiaChi());
		session.setAttribute("soDienThoai", giaoDien.getSoDienThoai());
		// TODO Auto-generated method stub
		return "login";
	}

	@RequestMapping("/actionlogin")
	public String actiondangnhap(ModelMap model, HttpServletRequest httpServletRequest, HttpServletResponse response,
	                             @RequestParam("username") String tendangnhap, @RequestParam("password") String matkhau) {
		matkhau = Common.encode(matkhau);
		List<Account> l = dangnhapservice.findUser(tendangnhap, matkhau);

		if (l.isEmpty()) {
			checklogin = 1;
			return "redirect:/login";
		} else {
			HttpSession session = httpServletRequest.getSession();
			saveLichSuDangNhap(tendangnhap);
			session.setAttribute("nguoidung", tendangnhap);
			session.setAttribute("chucvu", l.get(0).getChucVu().getMaChucVu() + "");// 1 giam doc 2 nhan vien
			if (l.get(0).getChucVu().getMaChucVu() == 3) {
				return "redirect:/";
			} else {
				return "redirect:/dptp";
			}
		}
	}

	public void saveLichSuDangNhap(String tendangnhap) {
		HistoryLogin lsdn = new HistoryLogin();
		Date date = new Date(System.currentTimeMillis());
		lsdn.setTaiKhoanDangNhap(tendangnhap);
		lsdn.setNgayDangNhap(date);
		lsdn.setGioDangNhap(date);
		lichSuDangNhapService.save(lsdn);
	}

	@RequestMapping("/dangxuat")
	public String dangxuat() {
		return "redirect:/login";
	}

	@RequestMapping("/doimatkhau")
	public String doimatkhau(HttpServletRequest httpServletRequest, ModelMap model,
	                         @ModelAttribute("taikhoan") Account taikhoan) {
		HttpSession session = httpServletRequest.getSession();
		Account gettaikhoan = dangnhapservice.findById(session.getAttribute("nguoidung").toString()).get();
		model.addAttribute("gettaikhoan", gettaikhoan);
		model.addAttribute("changeURL", "doimatkhau");
		model.addAttribute("titlepage", "Change Password");
		return "doimatkhau";
	}

	@RequestMapping("/actiondoimatkhau")
	public String actiondoimatkhau(HttpServletRequest httpServletRequest, ModelMap model,
	                               @ModelAttribute("taikhoan") ChangePassDto taikhoan) {
		String matkhaucu = Common.encode(taikhoan.getOldPass());
		String matkhaumoi = Common.encode(taikhoan.getNewPass());
		model.addAttribute("titlepage", "Change Password");
		model.addAttribute("gettaikhoan", taikhoan);
		Account gettaikhoan = dangnhapservice.findById(taikhoan.getTenDangNhap()).get();
		if (!matkhaucu.equals(gettaikhoan.getMatKhau())) {
			model.addAttribute("messageloi", "Current password is incorrect");
			taikhoan.setNewPass("");
			return "doimatkhau";
		} else if (!taikhoan.getNewPass().equals(taikhoan.getCfPass())) {
			model.addAttribute("messageloi", "Confirm the new password is incorrect");
			taikhoan.setNewPass("");
			return "doimatkhau";
		} else if (matkhaumoi.length() < 9) {
			taikhoan.setNewPass("");
			model.addAttribute("messageloi", "Password must be from 8 characters");
			return "doimatkhau";
		} else {
			gettaikhoan.setMatKhau(matkhaumoi);
			dangnhapservice.save(gettaikhoan);
			model.addAttribute("gettaikhoan", taikhoan);
			model.addAttribute("message", "Password changed successfully");
			taikhoan.setNewPass("");
			return "doimatkhau";
		}
	}

}