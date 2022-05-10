package action.member;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.*;

import action.CommandAction;

public class DeleteProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String mem_id=request.getParameter("mem_id"); //�Է�X
		String passwd=request.getParameter("passwd"); //�Է�O
		
		System.out.println("DeleteProcAction�� mem_id=>"+mem_id+", passwd=>"+passwd);
		//-------------------------------------------------------------------------
		MemberDAO memMgr=new MemberDAO();
		int delcheck=memMgr.memberDelete(mem_id, passwd); //ȸ��Ż�� �޼��� ȣ��
		System.out.println("DeleteProc.do�� ȸ��Ż�𼺰�����(delcheck)=>"+delcheck); //1Ż�� 0����
		
		request.setAttribute("delcheck", delcheck);
		
		return "/member/DeleteProc.jsp";
	}

}
