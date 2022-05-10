package action.mboard;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.MBoardDAO;
import beans.MBoardDTO;

import action.CommandAction;

public class M_DeleteProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum"); //hidden
		
		int m_number=Integer.parseInt(request.getParameter("m_number"));
		String m_title=request.getParameter("m_title");
		System.out.println("M_DeleteProcAction�� m_number=>"+m_number+", m_title=>"+m_title+", pageNum=>"+pageNum);
		
		MBoardDAO dbPro=new MBoardDAO();
		int check=dbPro.deleteArticle(m_number, m_title);
		
		request.setAttribute("pageNum", pageNum); //������ ���ڵ尡 �ִ� �������� �̵�
		request.setAttribute("check", check); //${check} ������ ������������
		
		return "/board/marvel/M_DeleteProc.jsp";
	}

}
