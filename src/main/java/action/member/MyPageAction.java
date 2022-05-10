package action.member;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.*;

import action.CommandAction;

public class MyPageAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		HttpSession session=request.getSession();
		String mem_id=(String)session.getAttribute("idKey"); //Object�̱� ������ String���� ����ȯ �������
		System.out.println("MyPageAction�� mem_id=>"+mem_id); //null�̶��? ...
		
		MemberDAO memMgr=new MemberDAO();
		memMgr.syncPG(mem_id);
		
		//���� ����� �ٲ�ٸ� �ݿ��� �� �ֵ���
		MemberDTO npg=memMgr.getNPG(mem_id);
		System.out.println("����� �ٽ� ��� ���� ��ü(npg)=>"+npg);
    	int mem_grade=npg.getMem_grade();
    	System.out.println("mem_grade=>"+mem_grade);
		request.setAttribute("mem_grade", mem_grade);
		
		return "/member/MyPage.jsp";
	}

}
