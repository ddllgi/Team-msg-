package action.notice;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.NoticeDAO;
import beans.NoticeDTO;

import action.CommandAction;

public class N_DeleteProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum"); //hidden
		//�߰�
		String passwd=request.getParameter("passwd"); //�Է�O //���� �ʿ� ������
		int notice_number=Integer.parseInt(request.getParameter("notice_number"));
		System.out.println("N_DeleteProcAction�� notice_number=>"+notice_number+", passwd=>"+passwd+", pageNum=>"+pageNum);
		
		NoticeDAO dbPro=new NoticeDAO();
		int check=dbPro.deleteArticle(notice_number);
		
		request.setAttribute("pageNum", pageNum); //������ ���ڵ尡 �ִ� �������� �̵�
		request.setAttribute("check", check); //${check} ������ ������������
		
		return "/board/notice/N_DeleteProc.jsp";
	}

}
