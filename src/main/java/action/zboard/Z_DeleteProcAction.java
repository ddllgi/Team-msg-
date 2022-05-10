package action.zboard;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.ZBoardDAO;
import beans.ZBoardDTO;

import action.CommandAction;

public class Z_DeleteProcAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum"); //hidden
		
		int z_number=Integer.parseInt(request.getParameter("z_number"));
		String z_title=request.getParameter("z_title");
		System.out.println("Z_DeleteProcAction�� z_number=>"+z_number+", z_title=>"+z_title+", pageNum=>"+pageNum);
		
		ZBoardDAO dbPro=new ZBoardDAO();
		int check=dbPro.deleteArticle(z_number, z_title);
		
		request.setAttribute("pageNum", pageNum); //������ ���ڵ尡 �ִ� �������� �̵�
		request.setAttribute("check", check); //${check} ������ ������������
		
		return "/board/zzang/Z_DeleteProc.jsp";
	}

}
