package action.hboard;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import beans.HBoardDAO;
import beans.HBoardDTO;
import beans.HCommentDAO;
import beans.NoticeDAO;
import beans.NoticeDTO;
import action.CommandAction;

public class H_ListAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum");
		//�߰�(�˻��о�, �˻���)
		String search=request.getParameter("search"); //�˻��о�
		String searchtext=request.getParameter("searchtext"); //�˻�
		
		String h_ref=request.getParameter("h_ref");
		
		System.out.println("H_ListAction������ �Ű����� Ȯ��");
		System.out.println("pageNum=>"+pageNum+", search=>"+search+", searchtext=>"+searchtext+", h_ref=>"+h_ref);
		
		int count=0; //�� ���ڵ��
		List<HBoardDTO> articleList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		HBoardDAO dbPro=new HBoardDAO();
		if (h_ref==null) {
			count=dbPro.getArticleSearchCount(search, searchtext);
		}else if (Integer.parseInt(h_ref)==1 || Integer.parseInt(h_ref)==2) {
			count=dbPro.getArticleSearchCount(search, searchtext, h_ref);
		}else if (Integer.parseInt(h_ref)==4)  {
			count=dbPro.getHotArticleSearchCount(search, searchtext);
		}
		System.out.println("���� ���ڵ��(count)=>"+count);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList=dbPro.pageList(pageNum, count);
		if (count > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList.get("endRow"));
			if (h_ref==null){
				articleList=dbPro.getBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext);
			}else if (Integer.parseInt(h_ref)==1 || Integer.parseInt(h_ref)==2) {
				articleList=dbPro.getBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext, h_ref);
			}else if (Integer.parseInt(h_ref)==4) {
				articleList=dbPro.getHotBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext);
			}
			System.out.println("H_ListAction�� articleList=>"+articleList);
		}else {
			articleList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		//2. ó���� ����� ����(���� �޸𸮿� ����)=>�̵��� �������� �����ؼ� ���(request)
		request.setAttribute("search", search); //${search}
		request.setAttribute("searchtext", searchtext); //
		request.setAttribute("pgList", pgList); //10���� ����¡ ó�� ����
		request.setAttribute("articleList", articleList); //����� ���ڵ尪�� ${articleList}
		request.setAttribute("h_ref", h_ref);
		
		//��������-------------------------------------------------------
		int count2=0; //�� ���ڵ��
		List<NoticeDTO> noticeList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		NoticeDAO dbPro2=new NoticeDAO();
		count2=dbPro2.getArticleSearchCount(search, searchtext);
		System.out.println("���� ���ڵ� ��(count2)=>"+count2);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList2=dbPro2.pageList(pageNum, count2);
		if (count2 > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList2.get("endRow"));
			noticeList=dbPro2.getBoardArticles(pgList2.get("startRow"), pgList2.get("endRow"), search, searchtext);
			System.out.println("N_ListAction�� noticeList=>"+noticeList);
		}else {
			noticeList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		request.setAttribute("pgList2", pgList2); //10���� ����¡ ó�� ����
		request.setAttribute("noticeList", noticeList); //����� ���ڵ尪�� ${articleList}
		
		
		return "/board/harry/H_List.jsp";
	}

}
