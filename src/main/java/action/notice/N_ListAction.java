package action.notice;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import beans.NoticeDAO;
import beans.NoticeDTO;

import action.CommandAction;

public class N_ListAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum");
		//�߰�(�˻��о�, �˻���)
		String search=request.getParameter("search"); //�˻��о�
		String searchtext=request.getParameter("searchtext"); //�˻�
		
		
		System.out.println("N_ListAction������ �Ű����� Ȯ��");
		System.out.println("pageNum=>"+pageNum+", search=>"+search+", searchtext=>"+searchtext);
		
		int count=0; //�� ���ڵ��
		List<NoticeDTO> articleList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		NoticeDAO dbPro=new NoticeDAO();
		count=dbPro.getArticleSearchCount(search, searchtext);
		System.out.println("���� ���ڵ��(count)=>"+count);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList=dbPro.pageList(pageNum, count);
		if (count > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList.get("endRow"));
			articleList=dbPro.getBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext);
			System.out.println("N_ListAction�� articleList=>"+articleList);
		}else {
			articleList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		//2. ó���� ����� ����(���� �޸𸮿� ����)=>�̵��� �������� �����ؼ� ���(request)
		request.setAttribute("search", search); //${search}
		request.setAttribute("searchtext", searchtext); //
		request.setAttribute("pgList", pgList); //10���� ����¡ ó�� ����
		request.setAttribute("articleList", articleList); //����� ���ڵ尪�� ${articleList}
		
		return "/board/notice/N_List.jsp";
	}

}
