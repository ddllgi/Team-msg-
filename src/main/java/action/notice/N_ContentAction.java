package action.notice;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import beans.NoticeDAO;
import beans.NoticeDTO;

import action.CommandAction;

public class N_ContentAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		int notice_number=Integer.parseInt(request.getParameter("notice_number"));
		String pageNum=request.getParameter("pageNum");
		System.out.println("N_ContentAction�� pageNum=>"+pageNum+", notice_number=>"+notice_number);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		
		NoticeDAO dbPro=new NoticeDAO();
		NoticeDTO article=dbPro.getArticle(notice_number);
		
		//String content=article.getContent().replace("\r\n", "<br>");
		//content=content.replace("\r\n", "<br>");
		
		
		//2. �������� ������ �޸𸮿� ����->request�� ����->jsp ${Ű��}���� �ҷ��� �� �ֵ��� ����
		request.setAttribute("notice_number", notice_number); //${num}
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("article", article); //${article.ref}, ${article.re_step} ~
		
		return "/board/notice/N_Content.jsp";
	}

}
