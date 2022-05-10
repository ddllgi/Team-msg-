package action.mboard;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import beans.MBoardDAO;
import beans.MBoardDTO;
import beans.NoticeDAO;
import beans.NoticeDTO;
//import beans.HCommentDAO;
import action.CommandAction;

public class M_ListAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		String pageNum=request.getParameter("pageNum");
		//�߰�(�˻��о�, �˻���)
		String search=request.getParameter("search"); //�˻��о�
		String searchtext=request.getParameter("searchtext"); //�˻�
		
		String m_ref=request.getParameter("m_ref");
		
		System.out.println("G_ListAction������ �Ű����� Ȯ��");
		System.out.println("pageNum=>"+pageNum+", search=>"+search+", searchtext=>"+searchtext+", m_ref=>"+m_ref);
		
		int count=0; //�� ���ڵ��
		List<MBoardDTO> articleList=null; //ȭ�鿡 ����� ���ڵ带 ������ ����
		
		MBoardDAO dbPro=new MBoardDAO();
		if (m_ref==null) {
			count=dbPro.getArticleSearchCount(search, searchtext);
		}else if (Integer.parseInt(m_ref)==1 || Integer.parseInt(m_ref)==2) {
			count=dbPro.getArticleSearchCount(search, searchtext, m_ref);
		}else if (Integer.parseInt(m_ref)==4)  {
			count=dbPro.getHotArticleSearchCount(search, searchtext);
		}
		System.out.println("���� ���ڵ��(count)=>"+count);
		//1. ȭ�鿡 ����� ��������ȣ   2. ����� ���ڵ� ����
		Hashtable<String, Integer> pgList=dbPro.pageList(pageNum, count);
		if (count > 0) {
			System.out.println(pgList.get("startRow")+", "+pgList.get("endRow"));
			if (m_ref==null){
				articleList=dbPro.getBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext);
			}else if (Integer.parseInt(m_ref)==1 || Integer.parseInt(m_ref)==2) {
				articleList=dbPro.getBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext, m_ref);
			}else if (Integer.parseInt(m_ref)==4) {
				articleList=dbPro.getHotBoardArticles(pgList.get("startRow"), pgList.get("endRow"), search, searchtext);
			}
			System.out.println("M_ListAction�� articleList=>"+articleList);
		}else {
			articleList=Collections.EMPTY_LIST; //����ִ� List��ü
		}
		
		//2. ó���� ����� ����(���� �޸𸮿� ����)=>�̵��� �������� �����ؼ� ���(request)
		request.setAttribute("search", search); //${search}
		request.setAttribute("searchtext", searchtext); //
		request.setAttribute("pgList", pgList); //10���� ����¡ ó�� ����
		request.setAttribute("articleList", articleList); //����� ���ڵ尪�� ${articleList}
		request.setAttribute("m_ref", m_ref);
		
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
		
		
		
		return "/board/marvel/M_List.jsp";
	}

}
