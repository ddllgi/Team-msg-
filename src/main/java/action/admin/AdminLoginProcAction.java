package action.admin;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.AdminDAO;
import beans.AdminDTO;
import action.CommandAction;

public class AdminLoginProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String mem_id=request.getParameter("admin_id");
	    String mem_passwd=request.getParameter("admin_passwd");
	    System.out.println("admin_id=>"+mem_id+", admin_passwd=>"+mem_passwd);
	    //->member->MemberDAO��ü�ʿ�->loginCheck()
	    AdminDAO memMgr=new AdminDAO();
	    boolean adminLoginCheck=memMgr.adminLoginCheck(mem_id, mem_passwd);
	    System.out.println("AdminLoginProc.do�� adminLoginCheck=>"+adminLoginCheck);
		
	    request.setAttribute("adminLoginCheck", new Boolean(adminLoginCheck));
	    request.setAttribute("mem_id", new String(mem_id)); //id���� ������ �����ϱ� ���� sessionó���� �� ��
    	request.setAttribute("mem_passwd", new String(mem_passwd)); //�ۿ� �ֱ� ���� ������ ����
    	
    	AdminDTO nick=memMgr.getAdminNickname(mem_id);
		System.out.println("nickname�� ���� ��ü(mem2)=>"+nick);
    	String admin_nickname=nick.getAdmin_nickname();
    	System.out.println("admin_nickname=>"+admin_nickname);
		
		request.setAttribute("mem_nickname", admin_nickname);
		
		return "/admin/AdminLoginProc.jsp";
	}

}
