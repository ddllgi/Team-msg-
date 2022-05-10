package action.member;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.BoardDAO;
import beans.BoardDTO;

import action.CommandAction;

public class Scrap_DeleteProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum"); //hidden
		int scrap_number=Integer.parseInt(request.getParameter("scrap_number"));
		int s_category=Integer.parseInt(request.getParameter("s_category"));
		int s_number=Integer.parseInt(request.getParameter("s_number"));
		String s_title=request.getParameter("s_title");
		System.out.println("Scrap_DeleteProcAction�� scrap_number=>"+scrap_number+", s_title=>"+s_title+", pageNum=>"+pageNum);
		
		BoardDAO dbPro=new BoardDAO();
		int check=dbPro.deleteScrapArticle(scrap_number, s_category, s_number);
		
		request.setAttribute("pageNum", pageNum); //������ ���ڵ尡 �ִ� �������� �̵�
		request.setAttribute("check", check); //${check} ������ ������������
		
		return "/member/Scrap_DeleteProc.jsp";
	}

}
