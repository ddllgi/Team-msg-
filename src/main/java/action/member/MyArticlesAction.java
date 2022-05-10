package action.member;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import beans.HBoardDAO;
import beans.HBoardDTO;
import beans.BoardDAO;
import beans.BoardDTO;
import beans.HCommentDAO;
import action.CommandAction;

import action.CommandAction;

public class MyArticlesAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		HttpSession session=request.getSession();
		String mem_id=(String)session.getAttribute("idKey"); //Object�̱� ������ String���� ����ȯ �������
		System.out.println("MyArticlesAction�� mem_id=>"+mem_id);
		
		String pageNum=request.getParameter("pageNum");
		//�߰�(�˻��о�, �˻���)
		//String search=request.getParameter("search"); //�˻��о�
		//String searchtext=request.getParameter("searchtext"); //�˻�
		String genre=request.getParameter("genre");
		
		//String h_ref=request.getParameter("h_ref");
		
		System.out.println("MyArticlesAction������ �Ű����� Ȯ��");
		System.out.println("pageNum=>"+pageNum+", genre=>"+genre);
		
		int count=0; //�� ���ڵ��
		List<BoardDTO> articleList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		BoardDAO dbPro=new BoardDAO();
		/*
		 * if (genre==null) { count=0; }else if (Integer.parseInt(genre)==10) {
		 * count=dbPro.getMyArticleCount("h", mem_id); }else if
		 * (Integer.parseInt(genre)==20) { count=dbPro.getMyArticleCount("z", mem_id);
		 * }else if (Integer.parseInt(genre)==30) { count=dbPro.getMyArticleCount("m",
		 * mem_id); }else if (Integer.parseInt(genre)==40) {
		 * count=dbPro.getMyArticleCount("g", mem_id); }else if
		 * (Integer.parseInt(genre)==50) { count=dbPro.getMyArticleCount("r", mem_id); }
		 */
		
		if (genre==null) {
			count=0;
		}else {
			count=dbPro.getMyArticleCount(genre, mem_id);
		}
		
		System.out.println("���� ���ڵ��(count)=>"+count);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList=dbPro.pageList(pageNum, count);
		/*
		 * if (count > 0) {
		 * System.out.println(pgList.get("startRow")+", "+pgList.get("endRow")); if
		 * (Integer.parseInt(genre)==10) {
		 * articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"),
		 * "h", mem_id); }else if (Integer.parseInt(genre)==20) {
		 * articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"),
		 * "z", mem_id); }else if (Integer.parseInt(genre)==30) {
		 * articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"),
		 * "m", mem_id); }else if (Integer.parseInt(genre)==40) {
		 * articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"),
		 * "g", mem_id); }else if (Integer.parseInt(genre)==50) {
		 * articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"),
		 * "r", mem_id); }
		 */
		
		if (count > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList.get("endRow"));
			articleList=dbPro.getMyArticles(pgList.get("startRow"), pgList.get("endRow"), genre, mem_id);
			
			System.out.println("MyArticlesAction�� articleList=>"+articleList);
		}else {
			articleList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		//2. ó���� ����� ����(���� �޸𸮿� ����)=>�̵��� �������� �����ؼ� ���(request)
		//request.setAttribute("search", search); //${search}
		//request.setAttribute("searchtext", searchtext); //
		request.setAttribute("pgList", pgList); //10���� ����¡ ó�� ����
		request.setAttribute("articleList", articleList); //����� ���ڵ尪�� ${articleList}
		//request.setAttribute("h_ref", h_ref);
		request.setAttribute("genre", genre);
		
		return "/member/MyArticles.jsp";
	}

}
