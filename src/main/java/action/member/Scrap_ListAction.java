package action.member;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import beans.BoardDAO;
import beans.BoardDTO;

import action.CommandAction;

public class Scrap_ListAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		HttpSession session=request.getSession();
		String mem_id=(String)session.getAttribute("idKey"); //Object�̱� ������ String���� ����ȯ �������
		System.out.println("Scrap_ListAction�� mem_id=>"+mem_id);
		
		String pageNum=request.getParameter("pageNum");
		
		
		System.out.println("Scrap_ListAction������ �Ű����� Ȯ��");
		System.out.println("pageNum=>"+pageNum);
		
		int count=0; //�� ���ڵ��
		List<BoardDTO> articleList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		BoardDAO dbPro=new BoardDAO();
		count=dbPro.getScrapArticleCount(mem_id);
		System.out.println("���� ���ڵ��(count)=>"+count);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList=dbPro.pageList(pageNum, count);
		if (count > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList.get("endRow"));
			articleList=dbPro.getScrapArticles(mem_id, pgList.get("startRow"), pgList.get("endRow"));
			System.out.println("Scrap_ListAction�� articleList=>"+articleList);
		}else {
			articleList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		//2. ó���� ����� ����(���� �޸𸮿� ����)=>�̵��� �������� �����ؼ� ���(request)
		request.setAttribute("pgList", pgList); //10���� ����¡ ó�� ����
		request.setAttribute("articleList", articleList); //����� ���ڵ尪�� ${articleList}
		request.setAttribute("pageNum", pageNum);
		
		return "/member/Scrap_List.jsp";
	}

}
