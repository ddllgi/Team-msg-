package beans;

import java.io.File;
import java.sql.*;
import java.util.*;
import beans.*;

public class RBoardDAO {
	
	private DBConnectionMgr pool=null; //1. ���ᰴü����
	//����
	private Connection con=null;
	private PreparedStatement pstmt=null;
	private ResultSet rs=null;
	private String sql="";
	
	//2. ������ ���ؼ� ����
	public RBoardDAO() {
		
		try {
			pool=DBConnectionMgr.getInstance();
			System.out.println("pool=>"+pool);
		}catch (Exception e) {
			System.out.println("DB���� ����=>"+e);
		}
	}

	// ��� �� ���
	public int getCommentCountForList(int r_number) throws Exception {

		int x = 0;

		con = pool.getConnection();
		System.out.println("con=>" + con);
		sql = "select count(*) from r_comment where r_number=? and r_cbody!='������ ����Դϴ�.&nbsp;'";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, r_number);
		rs = pstmt.executeQuery();
		if (rs.next()) { // �����ִ� ����� �ִٸ�
			x = rs.getInt(1); // ������=rs.get�ڷ���(�ʵ�� �Ǵ� �ε�����ȣ)=>���⼭�� �ʵ��X(�ʵ���� ���� ����)
		}
		pool.freeConnection(con, pstmt, rs);
		return x;
	}

	//article~
	private RBoardDTO makeArticleFromResult() throws Exception {
		
		RBoardDTO article=new RBoardDTO();
		RBoardDAO rboard=new RBoardDAO();
		
		article.setR_number(rs.getInt("r_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setR_nickname(rs.getString("r_nickname"));
		article.setR_title(rs.getString("r_title"));
		article.setR_body(rs.getString("r_body"));
		article.setR_date(rs.getTimestamp("r_date"));
		article.setR_count(rs.getInt("r_count"));
		article.setR_scrap(rs.getInt("r_scrap"));
		article.setR_category(rs.getInt("r_category")); //��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setR_ref(rs.getInt("r_ref")); //�Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		article.setR_comments(rboard.getCommentCountForList(rs.getInt("r_number")));
		
		return article;
	}
	
	//�޼��� �ۼ�~
	
	//�� ���ڵ�(�Խù�) ��
	public int getArticleCount() {
		
		int x=0;
		
		try {
			con=pool.getConnection();
			System.out.println("con=>"+con);
			sql="select count(*) from r_table";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			if (rs.next()) {
				x=rs.getInt(1);
			}
		}catch (Exception e) {
			System.out.println("getArticleCount() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	//�˻�� �ش�Ǵ� �� ���ڵ� ��
	public int getArticleSearchCount(String search, String searchtext) {
		
		int x=0;
		
		try {
			con=pool.getConnection();
			System.out.println("con=>"+con);
			//--------------------------------------------------------------------------
			if (search==null || search=="") { //�˻��о߸� ����X
				sql="select count(*) from r_table";
			}else { //�˻��о�(����, �ۼ���, ����+����)
				if (search.equals("r_title_r_body")) { //����+����
					sql="select count(*) from r_table where r_title like '%"+
						searchtext+"%' or r_body like '%"+searchtext+"%' ";
				}else { //����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql="select count(*) from r_table where "+search+" like '%"+
						searchtext+"%' ";
				}
			}
			System.out.println("getArticleSearchCount �˻���=>"+sql);
			//--------------------------------------------------------------------------
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			if (rs.next()) {
				x=rs.getInt(1);
				System.out.println("�˻��� �� ���ڵ� ��=>"+x);
			}
		}catch (Exception e) {
			System.out.println("getArticleSearchCount() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	// �˻�� �ش�Ǵ� �� ���ڵ� �� ref
	public int getArticleSearchCount(String search, String searchtext, String r_ref) {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			// --------------------------------------------------------------------------
			if (search == null || search == "") { // �˻��о߸� ����X
				sql = "select count(*) from r_table where r_ref=" + r_ref;
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("r_title_r_body")) { // ����+����
					sql = "select count(*) from r_table where r_ref=" + r_ref + " and r_title like '%" + searchtext
							+ "%' or r_body like '%" + searchtext + "%' ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from r_table where r_ref=" + r_ref + " and " + search + " like '%"
							+ searchtext + "%' ";
				}
			}
			System.out.println("getArticleSearchCount()r_ref �˻���=>" + sql);
			// --------------------------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
				System.out.println("�˻��� �� ���ڵ� ��=>" + x);
			}
		} catch (Exception e) {
			System.out.println("getArticleSearchCount()r_ref ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return x;
	}
	
	// �˻�� �ش�Ǵ� �� ���ڵ� �� HOT
	public int getHotArticleSearchCount(String search, String searchtext) {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			// --------------------------------------------------------------------------
			if (search == null || search == "") { // �˻��о߸� ����X
				sql = "select count(*) from r_table where r_count > 50";
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("r_title_r_body")) { // ����+����
					sql = "select count(*) from r_table where r_count > 50 and (r_title like '%" + searchtext + "%' or r_body like '%"
							+ searchtext + "%') ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from r_table where r_count > 50 and " + search + " like '%" + searchtext + "%' ";
				}
			}
			System.out.println("getArticleSearchCount �˻���=>" + sql);
			// --------------------------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
				System.out.println("�˻��� �� ���ڵ� ��=>" + x);
			}
		} catch (Exception e) {
			System.out.println("getHotArticleSearchCount() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return x;
	}
	
	
	
	
	
	
	//�۸����ȸ
	public List<RBoardDTO> getArticles(int start, int end) {
		
		List<RBoardDTO> articleList=null;
		
		try {
			con=pool.getConnection();
			sql="select * from (select r_table.*, rownum as rn from (select * from r_table order by r_number desc) r_table) where rn between ? and (? - 1)";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, start+end);
			rs=pstmt.executeQuery();
			
			if (rs.next()) {
				articleList=new ArrayList(end);
				do {
					RBoardDTO article=this.makeArticleFromResult();
					articleList.add(article);
				}while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("getArticles() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return articleList;
	}
	
	//�۸����ȸ-�˻���� ����
	public List<RBoardDTO> getBoardArticles(int start, int end, String search, String searchtext) {
		
		List<RBoardDTO> articleList=null;
		
		try {
			con=pool.getConnection();
			
			if (search==null || search=="") {
				sql="select * from (select r_table.*, rownum as rn from (select * from r_table order by r_number desc) r_table) where rn >= ? and rn <= ?";
				}else { //����+����
					if (search.equals("r_title_r_body")) { //����+����
						sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+"where r_title like '%"+searchtext+"%' or r_body like '%"+searchtext+"%') r_table) where rn >= ? and rn <= ?";
					}else {
						sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+" where "+search+" like '%"+searchtext+"%') r_table) where rn >= ? and rn <= ?";
					}
				}
			System.out.println("getBoardArticles()�� sql=>"+sql);
			//-------------------------------------------------------
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, start); //mysql�� start-1
			pstmt.setInt(2, end); //end-1??
			rs=pstmt.executeQuery();
			if (rs.next()) {
				articleList=new ArrayList(end);
				do {
					RBoardDTO article=this.makeArticleFromResult();
					articleList.add(article); //������ ����
				}while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("getBoardArticles() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return articleList;
	}
	
	//�۸����ȸ-�˻����, r_ref ����
		public List<RBoardDTO> getBoardArticles(int start, int end, String search, String searchtext, String r_ref) {
			
			List<RBoardDTO> articleList=null;
			
			try {
				con=pool.getConnection();
				
				if (search==null || search=="" || r_ref=="") {
					sql="select * from (select r_table.*, rownum as rn from (select * from r_table where r_ref="+r_ref+" order by r_number desc) r_table) where rn >= ? and rn <= ?";
					}else { //����+����
						if (search.equals("subject_content")) { //����+����
							sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+"where r_title like '%"+searchtext+"%' or r_body like '%"+searchtext+"%') r_table) where rn >= ? and rn <= ?";
						}else {
							sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+" where "+search+" like '%"+searchtext+"%') r_table) where rn >= ? and rn <= ?";
						}
					}
				System.out.println("getBoardArticles()r_ref �� sql=>"+sql);
				//-------------------------------------------------------
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, start); //mysql�� start-1
				pstmt.setInt(2, end); //end-1??
				rs=pstmt.executeQuery();
				if (rs.next()) {
					articleList=new ArrayList(end);
					do {
						RBoardDTO article=this.makeArticleFromResult();
						articleList.add(article); //������ ����
					}while (rs.next());
				}
			}catch (Exception e) {
				System.out.println("getBoardArticles()r_ref ���� �߻�=>"+e);
			}finally {
				pool.freeConnection(con, pstmt, rs);
			}
			
			return articleList;
		}
	
	
	//�۸����ȸ-�˻���� ����
	public List<RBoardDTO> getHotBoardArticles(int start, int end, String search, String searchtext) {
			
		List<RBoardDTO> articleList=null;
			
		try {
			con=pool.getConnection();
				
			if (search==null || search=="") {
				sql="select * from (select r_table.*, rownum as rn from (select * from r_table where r_count > 50 order by r_number desc) r_table) where rn >= ? and rn <= ?";
				}else { //����+����
					if (search.equals("subject_content")) { //����+����
						sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+"where r_count > 50 and (r_title like '%"+searchtext+"%' or r_body like '%"+searchtext+"%')) r_table) where rn >= ? and rn <= ?";
					}else {
						sql="select * from (select r_table.*, rownum as rn from (select * from r_table "+" where r_count > 50 "+search+" like '%"+searchtext+"%') r_table) where rn >= ? and rn <= ?";
					}
				}
			System.out.println("getHotBoardArticles()�� sql=>"+sql);
			//-------------------------------------------------------
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, start); //mysql�� start-1
			pstmt.setInt(2, end); //end-1??
			rs=pstmt.executeQuery();
			if (rs.next()) {
				articleList=new ArrayList(end);
				do {
					RBoardDTO article=this.makeArticleFromResult();
					articleList.add(article); //������ ����
				}while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("getBoardArticles() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
			
		return articleList;
	}	
		
		
		
		
		
	
	//����¡ ó�� ���
	public Hashtable pageList(String pageNum, int count) {
		
		Hashtable<String, Integer> pgList=new Hashtable<String, Integer>();
		
		int pageSize=10; //numPerPage=>�������� �����ִ� �Խù� ��(=���ڵ� ��) default:10
		int blockSize=10; //pagePerBlock=>���� �����ִ� �������� default:10
	
		//�Խ����� �� ó�� �����Ű�� ������ 1���������� ���->���� �ֱ��� ���� ������ ����
		if (pageNum==null) {
			pageNum="1"; //default(������ 1�������� �������� �ʾƵ� ������� �ϱ� ����)
		}
		int currentPage=Integer.parseInt(pageNum); //"1"->1(=nowPage)(���� ������)
		//				(1-1)*10+1=1, (2-1)*10+1=11, (3-1)*10+1=21
		int startRow=(currentPage-1)*pageSize+1; //���� ���ڵ� ��ȣ
		int endRow=currentPage*pageSize; //1*10=10, 2*10=20, 3*10=30
		int number=0; //beginPerPage->���������� �����ϴ� �� ó���� ������ �Խù���ȣ
		System.out.println("���� ���ڵ� ��(count)=>"+count);
		number=count-(currentPage-1)*pageSize;
		System.out.println(currentPage+"�������� number=>"+number);
		//��1�� list.jsp ����¡ ó�� ����
		//				122/10=12.2+1.0=13.2=13
		int pageCount=count/pageSize+(count%pageSize==0?0:1);
		//2. ���������� @@@@�߿�@@@@
		int startPage=0;
		if (currentPage%blockSize!=0) { //������ ���� 1~9, 11~19. 21~29 (10�� ����� �ƴ� ���)
			startPage=currentPage/blockSize*blockSize+1; //��輱 ������
		}else { //10%10=0(10,20,30,40,,,)
					 // ((10/10)-1)*10+1=1
			startPage=((currentPage/blockSize)-1)*blockSize+1;
		}
		//����������
		int endPage=startPage+blockSize-1; //1+10-1=10, 2+10-1=11
		System.out.println("startPage=>"+startPage+", endPage=>"+endPage);
		//������ �����ؼ� ��ũ �ɾ ���(������ ������ > �� ������ ��  �� �� ��... ���ƾ� ��)
		//11>10=>endPage=10
		if (endPage > pageCount) endPage=pageCount; //������ ������=�� ������ ��  �̵��� ������ֱ�
		//����¡ ó���� ���� �����->Hashtable, HashMap=>ListAction�� ����->list.jsp
		pgList.put("pageSize", pageSize); // <->pgList(Ű��)("pageSize")
		pgList.put("blockSize", blockSize);
		pgList.put("currentPage", currentPage);
		pgList.put("startRow", startRow);
		pgList.put("endRow", endRow);
		pgList.put("count", count);
		pgList.put("number", number);
		pgList.put("startPage", startPage);
		pgList.put("endPage", endPage);
		pgList.put("pageCount", pageCount);
		
		return pgList;
	}
	
	//�۾���
	public void insertArticle(RBoardDTO article) {

		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int r_number = article.getR_number();
		
		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertArticle()�޼��� ���� r_number=>" + r_number);
		
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(r_number) from r_table";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				number = rs.getInt(1) + 1; // �ִ밪+1
				System.out.println("�� ��ȣ(number)=>" + number);
			} else {
				number = 1;
			}
			con.commit();
			
			sql = "insert into r_table(r_number, mem_id, r_nickname, r_title, r_body, r_date, r_category, r_ref) ";
			sql += " values(?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, article.getMem_id());
			pstmt.setString(3, article.getR_nickname());
			pstmt.setString(4, article.getR_title());
			pstmt.setString(5, article.getR_body());
			pstmt.setTimestamp(6, article.getR_date());
			pstmt.setInt(7, article.getR_category());
			pstmt.setInt(8, article.getR_ref());

			int insert = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �۾��� ��������(insert)=>" + insert + " //(1���� 0����)");
			
			if (insert > 0) {
				String sql2 = "update login set log_point=log_point+10 where mem_id=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setString(1, article.getMem_id());

				int pointup = pstmt.executeUpdate();
				con.commit();
				System.out.println("�� �ۼ� ����Ʈ ��������(pointup)=>" + pointup);

				if (pointup > 0) {
					String sql3 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql3);
					pstmt.setString(1, article.getMem_id());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>" + point + ", ���(grade)=>" + grade);

						String sql4 = "";
						if (point < 100) {
							sql4 = "update login set log_grade=1 where mem_id=?";
						} else if (point >= 100 && point < 500) {
							sql4 = "update login set log_grade=2 where mem_id=?";
						} else if (point >= 500 && point < 2000) {
							sql4 = "update login set log_grade=3 where mem_id=?";
						} else if (point >= 2000 && point < 10000) {
							sql4 = "update login set log_grade=4 where mem_id=?";
						} else if (point >= 10000) {
							sql4 = "update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql4=>" + sql4);
						pstmt = con.prepareStatement(sql4);
						pstmt.setString(1, article.getMem_id());

						int gradeup = pstmt.executeUpdate();
						con.commit();
						System.out.println("�� �ۼ� �� ��� ��������(gradeup)=>" + gradeup);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("insertArticle()�޼��� ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

	}
	
	//�� ���� ��ȸ
	public RBoardDTO getArticle(int num) {
		
		RBoardDTO article=null;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			
			sql="update r_table set r_count=r_count+1 where r_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			int update=pstmt.executeUpdate();
			con.commit();
			System.out.println("��ȸ�� ��������(update)=>"+update+" //(1���� 0����)");
			
			sql="select * from r_table where r_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs=pstmt.executeQuery();
			
			if (rs.next()) { //�����ִ� ����� �ִٸ�
				article=this.makeArticleFromResult();
				con.commit();
			}
		}catch (Exception e) {
			System.out.println("getArticle() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return article;
	}
	
	//�ۼ���-������ ������ ã��
	public RBoardDTO updateGetArticle(int num) {
		
		RBoardDTO article=null;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select * from r_table where r_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				article=makeArticleFromResult();
				con.commit();
			}
		}catch (Exception e) {
			System.out.println("updateGetArticle() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return article;
	}
	
	//�ۼ���
	public int updateArticle(RBoardDTO article) {
		
		int x=-1; //�Խù� �������� 1���� 0����
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			sql="update r_table set r_title=?, r_body=?, r_category=?, r_ref=? where r_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, article.getR_title());
			pstmt.setString(2, article.getR_body());
			pstmt.setInt(3, article.getR_category());
			pstmt.setInt(4,article.getR_ref());
			pstmt.setInt(5, article.getR_number());
					
			int update=pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۼ��� ���� ����(update)=>"+update+" //(1���� 0����)");
			x=1; //��������
		}catch (Exception e) {
			System.out.println("updateArticle() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x; 
	}
	
	//�ۻ���
	public int deleteArticle(int r_number, String r_title) {
		
		int x=-1;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
				
			sql="delete from r_table where r_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, r_number);
					
			int delete1=pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� ��������=>"+delete1+" //(1���� 0����)");
				
			String sql2="delete from r_comment where r_number=?";
			pstmt=con.prepareStatement(sql2);
			pstmt.setInt(1, r_number);
					
			int delete2=pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� �� ��� ���� ��������=>"+delete2+" //(1���� 0����)");
				
			String sql3="delete from scrap where s_number=? and s_title=?";
			pstmt=con.prepareStatement(sql3);
			pstmt.setInt(1, r_number);
			pstmt.setString(2, r_title);
				
			int delete3=pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� �� ��ũ�� ���� ��������=>"+delete3+" //(1���� 0����)");
				
			x=1;
		}catch (Exception e) {
			System.out.println("deleteArticle() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
	
	
	
	
	
	
	
}
