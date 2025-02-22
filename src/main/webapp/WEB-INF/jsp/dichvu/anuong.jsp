<%@page import="FPTHotel.Controller.DvController"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="frm" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<frm:form action="actionanuong" modelAttribute="donDichVu">
	<table style="width: 100%;">
		<frm:hidden path="datPhong.maDatPhong" value="${madatphong }" />
		<tr>
			<td>Select the product:</td>
			<td><frm:select class="form-control input-sm"
					path="dichVu.maDichVu" id="refresh" name="refresh"
					onchange="select();">
					<c:forEach var="u" items="${ltendichvuanuong }">
						<frm:option value="${u.maDichVu }" label="${u.tenDichVu }" />
					</c:forEach>
				</frm:select></td>
		</tr>
		<tr>
			<td>Quantity:</td>
			<td><frm:input class="form-control input-sm" type="number"
					path="soLuong" min="1" max="99"
					oninput="checkMaxLenghtNumber(this,6)" required="required" /></td>
		</tr>
		<tr>
			<td></td>
			<td><frm:button type="submit" class="btn btn-success btn-xs">Add</frm:button></td>
		</tr>
	</table>
</frm:form>
