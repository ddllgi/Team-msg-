package action.mboard;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import action.CommandAction;

public class M_WriteFormAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		String pageNum=request.getParameter("pageNum");
		
		int m_number=0;

		//noticeContent.jsp���� �Ű������� ���޵�
		if (request.getParameter("m_number")!=null) {
			m_number=Integer.parseInt(request.getParameter("m_number"));
			
			System.out.println("M_Content.do���� �Ѿ�� �Ű����� Ȯ��");
			System.out.println("m_number=>"+m_number);
		}
		System.out.println("m_number�� null�� ��-->> m_number=>"+m_number);
		
		request.setAttribute("m_number", m_number); //${num}
		
		return "/board/marvel/M_WriteForm.jsp";
	}

}
