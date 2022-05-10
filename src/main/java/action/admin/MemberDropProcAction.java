package action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.CommandAction;
import beans.MemberDAO;

public class MemberDropProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String member_id=request.getParameter("member_id");
		System.out.println("MemberDropAction�� member_id=>"+member_id);
		
		MemberDAO memMgr=new MemberDAO();
		
		//�ۻ��� or �� ���̵� ����
		
		
		int delcheck=memMgr.memberDrop(member_id); //ȸ��Ż�� �޼��� ȣ��
		System.out.println("MemberDropProcAction�� ȸ��������������(delcheck)=>"+delcheck); //1Ż�� 0����
		request.setAttribute("member_id", member_id);
		request.setAttribute("delcheck", delcheck);
		
		return "/admin/MemberDropProc.jsp";
	}

}
