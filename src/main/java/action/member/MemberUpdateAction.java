package action.member;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.*;

import action.CommandAction;

public class MemberUpdateAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		
		response.setHeader("Cache-Control", "no-cache"); //��û�� �޸𸮿� ����X
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0); //�����Ⱓ 0->�������� ����
		
		HttpSession session=request.getSession();
		String mem_id=(String)session.getAttribute("idKey"); //Object�̱� ������ String���� ����ȯ �������
		System.out.println("MemberUpdate.do�� mem_id=>"+mem_id); //null�̶��? ...
		
		MemberDAO memMgr=new MemberDAO();
		MemberDTO mem=memMgr.getMember(mem_id);
		System.out.println("MemberUpdate.do�� ��ü(mem)=>"+mem);
		
		memMgr.syncPG(mem_id);
		
		request.setAttribute("mem_id", new String(mem_id)); //id���� ������ �����ϱ� ���� sessionó���� �� ��
		request.setAttribute("mem", mem);
		
		return "/member/MemberUpdate.jsp";
	}

}
