package action.hboard;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import action.CommandAction;

public class H_WriteFormAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		String pageNum=request.getParameter("pageNum");
		
		int h_number=0;

		//noticeContent.jsp���� �Ű������� ���޵�
		if (request.getParameter("h_number")!=null) {
			h_number=Integer.parseInt(request.getParameter("h_number"));
			
			System.out.println("H_Content.do���� �Ѿ�� �Ű����� Ȯ��");
			System.out.println("h_number=>"+h_number);
		}
		System.out.println("h_number�� null�� ��-->> h_number=>"+h_number);
		
		request.setAttribute("h_number", h_number); //${num}
		
		return "/board/harry/H_WriteForm.jsp";
	}

}
