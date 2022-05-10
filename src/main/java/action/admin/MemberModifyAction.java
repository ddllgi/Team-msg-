package action.admin;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.*;

import action.CommandAction;

public class MemberModifyAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		
		response.setHeader("Cache-Control", "no-cache"); //��û�� �޸𸮿� ����X
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0); //�����Ⱓ 0->�������� ����
		
		HttpSession session=request.getSession();
		String mem_id=(String)session.getAttribute("idKey"); //Object�̱� ������ String���� ����ȯ �������
		System.out.println("MemberModifyAction�� mem_id=>"+mem_id); //null�̶��? ...
		String member_id=request.getParameter("member_id");
		System.out.println("MemberModifyAction�� member_id=>"+member_id);
		
		MemberDAO memMgr=new MemberDAO();
		MemberDTO mem=memMgr.getMember(member_id);
		System.out.println("MemberModifyAction�� ��ü(mem)=>"+mem);
		
		memMgr.syncPG(member_id);
		
		request.setAttribute("mem_id", mem_id); //id���� ������ �����ϱ� ���� sessionó���� �� ��
		request.setAttribute("member_id", member_id);
		request.setAttribute("mem", mem);
		
		return "/admin/MemberModify.jsp";
	}

}
