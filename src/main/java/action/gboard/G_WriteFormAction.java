package action.gboard;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import action.CommandAction;

public class G_WriteFormAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		String pageNum=request.getParameter("pageNum");
		
		int g_number=0;

		//noticeContent.jsp���� �Ű������� ���޵�
		if (request.getParameter("g_number")!=null) {
			g_number=Integer.parseInt(request.getParameter("g_number"));
			
			System.out.println("G_Content.do���� �Ѿ�� �Ű����� Ȯ��");
			System.out.println("g_number=>"+g_number);
		}
		System.out.println("g_number�� null�� ��-->> g_number=>"+g_number);
		
		request.setAttribute("g_number", g_number); //${num}
		
		return "/board/ghibli/G_WriteForm.jsp";
	}

}
