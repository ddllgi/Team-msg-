package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BoardDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	// 2. ������ ���ؼ� ����
	public BoardDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	

	// H
	private BoardDTO makeHArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setNumber(rs.getInt("h_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setNickname(rs.getString("h_nickname"));
		article.setTitle(rs.getString("h_title"));
		article.setBody(rs.getString("h_body"));
		article.setDate(rs.getTimestamp("h_date"));
		article.setCount(rs.getInt("h_count"));
		article.setScrap(rs.getInt("h_scrap"));
		article.setCategory(rs.getInt("h_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setRef(rs.getInt("h_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		
		HBoardDAO hboard=new HBoardDAO();
		article.setComments(hboard.getCommentCountForList(rs.getInt("h_number")));

		return article;
	}

	// Z
	private BoardDTO makeZArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setNumber(rs.getInt("z_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setNickname(rs.getString("z_nickname"));
		article.setTitle(rs.getString("z_title"));
		article.setBody(rs.getString("z_body"));
		article.setDate(rs.getTimestamp("z_date"));
		article.setCount(rs.getInt("z_count"));
		article.setScrap(rs.getInt("z_scrap"));
		article.setCategory(rs.getInt("z_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setRef(rs.getInt("z_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		
		ZBoardDAO zboard=new ZBoardDAO();
		article.setComments(zboard.getCommentCountForList(rs.getInt("z_number")));
		
		return article;
	}

	// M
	private BoardDTO makeMArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setNumber(rs.getInt("m_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setNickname(rs.getString("m_nickname"));
		article.setTitle(rs.getString("m_title"));
		article.setBody(rs.getString("m_body"));
		article.setDate(rs.getTimestamp("m_date"));
		article.setCount(rs.getInt("m_count"));
		article.setScrap(rs.getInt("m_scrap"));
		article.setCategory(rs.getInt("m_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setRef(rs.getInt("m_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		
		MBoardDAO mboard=new MBoardDAO();
		article.setComments(mboard.getCommentCountForList(rs.getInt("m_number")));

		return article;
	}

	// G
	private BoardDTO makeGArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setNumber(rs.getInt("g_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setNickname(rs.getString("g_nickname"));
		article.setTitle(rs.getString("g_title"));
		article.setBody(rs.getString("g_body"));
		article.setDate(rs.getTimestamp("g_date"));
		article.setCount(rs.getInt("g_count"));
		article.setScrap(rs.getInt("g_scrap"));
		article.setCategory(rs.getInt("g_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setRef(rs.getInt("g_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		
		GBoardDAO gboard=new GBoardDAO();
		article.setComments(gboard.getCommentCountForList(rs.getInt("g_number")));

		return article;
	}

	// R
	private BoardDTO makeRArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setNumber(rs.getInt("r_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setNickname(rs.getString("r_nickname"));
		article.setTitle(rs.getString("r_title"));
		article.setBody(rs.getString("r_body"));
		article.setDate(rs.getTimestamp("r_date"));
		article.setCount(rs.getInt("r_count"));
		article.setScrap(rs.getInt("r_scrap"));
		article.setCategory(rs.getInt("r_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setRef(rs.getInt("r_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		
		RBoardDAO rboard=new RBoardDAO();
		article.setComments(rboard.getCommentCountForList(rs.getInt("r_number")));

		return article;
	}

	// ���� �� �� ��ƺ���
	// ���ڵ� ��
	public int getMyArticleCount(String init, String mem_id) {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from " + init + "_table where mem_id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("getMyArticleCount() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return x;
	}

	// �� ��� ��ȸ
	public List<BoardDTO> getMyArticles(int start, int end, String init, String mem_id) {

		List<BoardDTO> articleList = null;

		try {
			con = pool.getConnection();
			sql = "select * from (select " + init + "_table.*, rownum as rn from (select * from " + init
					+ "_table where mem_id='"+mem_id+"' order by " + init + "_number desc) " + init
					+ "_table) where rn >= ? and rn <=?";
			pstmt = con.prepareStatement(sql);
			//pstmt.setString(1, mem_id);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				articleList = new ArrayList(end);
				if (init.equals("h")) {
					do {
						BoardDTO article = this.makeHArticleFromResult();
						articleList.add(article);
					} while (rs.next());
				}else if (init.equals("z")) {
					do {
						BoardDTO article = this.makeZArticleFromResult();
						articleList.add(article);
					} while (rs.next());
				}else if (init.equals("m")) {
					do {
						BoardDTO article = this.makeMArticleFromResult();
						articleList.add(article);
					} while (rs.next());
				}else if (init.equals("g")) {
					do {
						BoardDTO article = this.makeGArticleFromResult();
						articleList.add(article);
					} while (rs.next());
				}else if (init.equals("r")) {
					do {
						BoardDTO article = this.makeRArticleFromResult();
						articleList.add(article);
					} while (rs.next());
				}
			}
			
		} catch (Exception e) {
			System.out.println("getMyArticles() ���� �߻�=>" + e);
		} finally {
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
		
		
		
	//--------------------------------------------------------------------------------------------------------
	// Scrap
	private BoardDTO makeSArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setScrap_number(rs.getInt("scrap_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setS_number(rs.getInt("s_number"));
		article.setS_category(rs.getInt("s_category"));
		article.setS_title(rs.getString("s_title"));
		article.setS_date(rs.getTimestamp("s_date"));
		article.setS_url(rs.getString("s_url"));
		article.setS_nickname(rs.getString("s_nickname"));

		return article;
	}
		
		
		
	//url �����
	public String makeScrapUrl(int s_category, int s_number) {
		
		String scrapurl="";
		
		if (s_category>=10 && s_category<20) {
			scrapurl="H_Content.do?h_number="+s_number;
		}else if (s_category>=20 && s_category<30) {
			scrapurl="Z_Content.do?z_number="+s_number;
		}else if (s_category>=30 && s_category<40) {
			scrapurl="M_Content.do?m_number="+s_number;
		}else if (s_category>=40 && s_category<50) {
			scrapurl="G_Content.do?g_number="+s_number;
		}else if (s_category>=50 && s_category<60) {
			scrapurl="R_Content.do?r_number="+s_number;
		}
		
		return scrapurl;
	}
	
	//
	public String makeInit(int s_category) {
		
		String init="";
		
		if (s_category>=10 && s_category<20) {
			init="h";
		}else if (s_category>=20 && s_category<30) {
			init="z";
		}else if (s_category>=30 && s_category<40) {
			init="m";
		}else if (s_category>=40 && s_category<50) {
			init="g";
		}else if (s_category>=50 && s_category<60) {
			init="r";
		}
		
		return init;
	}
	
	
	//��ũ��
	public void scrap(BoardDTO article) {
		
		//���� ����:��ũ�� ����, ���̵�, ��ũ���� ���� ���� ��ȣ(h_number~), ��ũ���� ���� ī�װ�, ��ũ���� ���� ����, ��ũ�� ��¥
		
		int scrap_number=article.getScrap_number();
		
		int number=0;
		System.out.println("scrap()�޼��� ���� scrap_number=>"+scrap_number);
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select max(scrap_number) from scrap";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				number=rs.getInt(1)+1;
				System.out.println("��ũ�� ����(number)=>"+number);
			}else {
				number=1;
			}
			con.commit();
			
			sql="insert into scrap(scrap_number, mem_id, s_number, s_category, s_title, s_date, s_url, s_nickname)";
			sql+=" values(?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, number);
			pstmt.setString(2, article.getMem_id());
			pstmt.setInt(3, article.getS_number());
			pstmt.setInt(4, article.getS_category());
			pstmt.setString(5, article.getS_title());
			pstmt.setTimestamp(6, article.getS_date());
			pstmt.setString(7, article.getS_url());
			pstmt.setString(8, article.getS_nickname());
			
			int scrap=pstmt.executeUpdate();
			con.commit();
			System.out.println("��ũ�� ��������(scrap)=>"+scrap);
			
			//��ũ���� ����
			int s_category=article.getS_category();
			String init=this.makeInit(s_category);
			System.out.println("init=>"+init);
			
			if (scrap > 0) {
				sql="update "+init+"_table set "+init+"_scrap="+init+"_scrap+1 where "+init+"_number=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, article.getS_number());
				
				int update=pstmt.executeUpdate();
				con.commit();
				System.out.println("��ũ���� ��������(update)=>"+update+" //(1���� 0����)");
			}
		}catch (Exception e) {
			System.out.println("scrap()�޼��� ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
	}
	
	
	//��ũ�� ���ڵ� ��
	public int getScrapArticleCount(String mem_id) {
		
		int x=0;
		
		try {
			con=pool.getConnection();
			System.out.println("con=>"+con);
			sql="select count(*) from scrap where mem_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			rs=pstmt.executeQuery();
			
			if (rs.next()) {
				x=rs.getInt(1);
			}
		}catch (Exception e) {
			System.out.println("getScrapArticleCount() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	
	//��ũ�� ��� ��ȸ
	public List<BoardDTO> getScrapArticles(String mem_id, int start, int end) {
		
		List<BoardDTO> articleList=null;
		
		try {
			con=pool.getConnection();
			sql="select * from (select scrap.*, rownum as rn from (select * from scrap where mem_id=? order by scrap_number desc) scrap) where rn >= ? and rn <= ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			rs=pstmt.executeQuery();
			
			if (rs.next()) {
				articleList=new ArrayList(end);
				do {
					BoardDTO article=this.makeSArticleFromResult();
					articleList.add(article);
				}while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("getScrapArticles() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return articleList;
	}
	
	//��ũ�� ����
	public int deleteScrapArticle(int scrap_number, int s_category, int s_number) {
		
		int x=-1;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
				
			sql="delete from scrap where scrap_number=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, scrap_number);
					
			int delete=pstmt.executeUpdate();
			con.commit();
			System.out.println("��ũ�� �ۻ��� ��������=>"+delete+" //(1���� 0����)");
			
			//��ũ���� ����
			String init=this.makeInit(s_category);
			System.out.println("init=>"+init);
			
			if (delete > 0) {
				sql="update "+init+"_table set "+init+"_scrap="+init+"_scrap-1 where "+init+"_number=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, s_number);
				
				int update=pstmt.executeUpdate();
				con.commit();
				System.out.println("��ũ���� ���ҿ���(update)=>"+update+" //(1���� 0����)");
			}
			
			
			
			x=1;
		}catch (Exception e) {
			System.out.println("deleteScrapArticle() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
	
	
	//����
	
	// Rate
	private BoardDTO makeRateArticleFromResult() throws Exception {

		BoardDTO article = new BoardDTO();

		article.setV_number(rs.getInt("v_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setV_movie(rs.getInt("v_movie"));
		article.setV_star(rs.getDouble("v_star"));
		article.setV_like(rs.getInt("v_like"));

		System.out.println("makeRateArticleFromResult()�� article=>"+article);
		return article;
	}
	
	//���� ��ճ���
	public double avgStar(int v_movie) {
		
		double x=0;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select round(avg(v_star), 2) from star where v_movie=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, v_movie);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				x=rs.getDouble(1);
			}
			con.commit();
		}catch (Exception e) {
			System.out.println("avgStar() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	//���ƿ� ����
	public int countLikes(int v_movie) {
		
		int x=0;
		
		try {
			con=pool.getConnection();
			sql="select sum(v_like) from star where v_movie=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, v_movie);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				x=rs.getInt(1);
			}
		}catch (Exception e) {
			System.out.println("countLikes() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	
	
	//�� ���� ǥ��
	public BoardDTO getMyRate(String mem_id, int v_movie) {
		
		 BoardDTO article=null;
		 
		 try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select * from star where mem_id=? and v_movie=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			pstmt.setInt(2, v_movie);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				article=makeRateArticleFromResult();
				con.commit();
				System.out.println("getMyRate()�� article=>"+article);
			}
		 }catch (Exception e) {
			 System.out.println("getMyRate() ���� �߻�=>"+e);
		 }finally {
			 pool.freeConnection(con, pstmt, rs);
		 }
		 
		 return article;
	}
	
	
	
	
	//����, ���ƿ� �Է�
	public void rate(BoardDTO article) {
		
		int v_number=article.getV_number();
		
		int number=0;
		System.out.println("rank() �޼��� ���� v_number=>"+v_number);
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			
			sql="select * from star where mem_id=? and v_movie=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, article.getMem_id());
			pstmt.setInt(2, article.getV_movie());
			rs=pstmt.executeQuery();
			if (rs.next()) { //�����Ͱ� ������->����
				//������ ������ ã�� �޼��� �ؿ� �ۼ��ϱ�
				sql="update star set v_star=?, v_like=? where mem_id=? and v_movie=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setDouble(1, article.getV_star());
				pstmt.setInt(2, article.getV_like());
				pstmt.setString(3, article.getMem_id());
				pstmt.setInt(4, article.getV_movie());
				
				int update=pstmt.executeUpdate();
				con.commit();
				System.out.println("���� or ���ƿ� ���� ��������(update)=>"+update);
				
				//������ ���� �޼��忡�� ��ȯ���� ����... ��� ����?
			}else { //�����Ͱ� ������->���� �Է�
				sql="select max(v_number) from star";
				pstmt=con.prepareStatement(sql);
				rs=pstmt.executeQuery();
				if (rs.next()) {
					number=rs.getInt(1)+1;
					System.out.println("���� �ۼ� ��ȣ(number)=>"+number);
				}else {
					number=1;
				}
				con.commit();
				
				sql="insert into star(v_number, mem_id, v_movie, v_star, v_like) ";
				sql+=" values(?, ?, ?, ?, ?)";
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, number);
				pstmt.setString(2, article.getMem_id());
				pstmt.setInt(3, article.getV_movie());
				pstmt.setDouble(4, article.getV_star());
				pstmt.setInt(5, article.getV_like());
				
				int insert=pstmt.executeUpdate();
				con.commit();
				System.out.println("���� or ���ƿ� ��� ��������(insert)=>"+insert);
			}
			
		}catch (Exception e) {
			System.out.println("rate() �޼��� ���� �߻�=>"+e);
		}finally {
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
