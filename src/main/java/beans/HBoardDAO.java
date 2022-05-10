package beans;

import java.io.File;
import java.sql.*;
import java.util.*;
import beans.HBoardDTO;

public class HBoardDAO {

	private DBConnectionMgr pool = null;
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	// 2. ������ ���ؼ� ����
	public HBoardDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	private HBoardDTO makeArticleFromResult() throws Exception {

		HBoardDTO article = new HBoardDTO();
		HBoardDAO hboard = new HBoardDAO();
		article.setH_number(rs.getInt("h_number"));
		article.setMem_id(rs.getString("mem_id"));
		article.setH_nickname(rs.getString("h_nickname"));
		article.setH_title(rs.getString("h_title"));
		article.setH_body(rs.getString("h_body"));
		article.setH_date(rs.getTimestamp("h_date"));
		article.setH_count(rs.getInt("h_count"));
		article.setH_scrap(rs.getInt("h_scrap"));
		article.setH_category(rs.getInt("h_category")); // ��ī�װ�->11:�ظ�����1, 12:�ظ�����2, 13:�ظ�����3,,,
		article.setH_ref(rs.getInt("h_ref")); // �Խ��Ǻз�->1:����, 2:����, 3:����, 4:hot
		article.setH_comments(hboard.getCommentCountForList(rs.getInt("h_number")));

		return article;
	}

	// �޼��� �ۼ�~

	// �� ���ڵ�(�Խù�) ��
	public int getArticleCount() {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from h_table";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("getArticleCount() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return x;
	}

	// �˻�� �ش�Ǵ� �� ���ڵ� ��
	public int getArticleSearchCount(String search, String searchtext) {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			// --------------------------------------------------------------------------
			if (search == null || search == "") { // �˻��о߸� ����X
				sql = "select count(*) from h_table";
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("h_title_h_body")) { // ����+����
					sql = "select count(*) from h_table where h_title like '%" + searchtext + "%' or h_body like '%"
							+ searchtext + "%' ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from h_table where " + search + " like '%" + searchtext + "%' ";
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
			System.out.println("getArticleSearchCount() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return x;
	}

	// �˻�� �ش�Ǵ� �� ���ڵ� �� ref
	public int getArticleSearchCount(String search, String searchtext, String h_ref) {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			// --------------------------------------------------------------------------
			if (search == null || search == "") { // �˻��о߸� ����X
				sql = "select count(*) from h_table where h_ref=" + h_ref;
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("h_title_h_body")) { // ����+����
					sql = "select count(*) from h_table where h_ref=" + h_ref + " and h_title like '%" + searchtext
							+ "%' or h_body like '%" + searchtext + "%' ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from h_table where h_ref=" + h_ref + " and " + search + " like '%"
							+ searchtext + "%' ";
				}
			}
			System.out.println("getArticleSearchCount()h_ref �˻���=>" + sql);
			// --------------------------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
				System.out.println("�˻��� �� ���ڵ� ��=>" + x);
			}
		} catch (Exception e) {
			System.out.println("getArticleSearchCount()h_ref ���� �߻�=>" + e);
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
				sql = "select count(*) from h_table where h_count > 50";
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("h_title_h_body")) { // ����+����
					sql = "select count(*) from h_table where h_count > 50 and (h_title like '%" + searchtext
							+ "%' or h_body like '%" + searchtext + "%') ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from h_table where h_count > 50 and " + search + " like '%" + searchtext
							+ "%' ";
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

	// �۸����ȸ //�̰� �� ��
	public List<HBoardDTO> getArticles(int start, int end) {

		List<HBoardDTO> articleList = null;

		try {
			con = pool.getConnection();
			sql = "select * from (select h_table.*, rownum as rn from (select * from h_table order by h_number desc) h_table) where rn between ? and (? - 1)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, start + end);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				articleList = new ArrayList(end);
				do {
					HBoardDTO article = this.makeArticleFromResult();
					articleList.add(article);
				} while (rs.next());
			}
		} catch (Exception e) {
			System.out.println("getArticles() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return articleList;
	}

	// ��� �� ���
	public int getCommentCountForList(int h_number) throws Exception {

		int x = 0;

		con = pool.getConnection();
		System.out.println("con=>" + con);
		sql = "select count(*) from h_comment where h_number=? and h_cbody!='������ ����Դϴ�.&nbsp;'";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, h_number);
		rs = pstmt.executeQuery();
		if (rs.next()) { // �����ִ� ����� �ִٸ�
			x = rs.getInt(1); // ������=rs.get�ڷ���(�ʵ�� �Ǵ� �ε�����ȣ)=>���⼭�� �ʵ��X(�ʵ���� ���� ����)
		}
		pool.freeConnection(con, pstmt, rs);
		return x;
	}

	// �۸����ȸ-�˻���� ����
	public List<HBoardDTO> getBoardArticles(int start, int end, String search, String searchtext) {

		List<HBoardDTO> articleList = null;

		try {
			con = pool.getConnection();

			if (search == null || search == "") {
				sql = "select * from (select h_table.*, rownum as rn from (select * from h_table order by h_number desc) h_table) where rn >= ? and rn <= ?";
			} else { // ����+����
				if (search.equals("h_title_h_body")) { // ����+����
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table "
							+ "where h_title like '%" + searchtext + "%' or h_body like '%" + searchtext
							+ "%') h_table) where rn >= ? and rn <= ?";
				} else {
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table " + " where "
							+ search + " like '%" + searchtext + "%') h_table) where rn >= ? and rn <= ?";
				}
			}
			System.out.println("getBoardArticles()�� sql=>" + sql);
			// -------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start); // mysql�� start-1
			pstmt.setInt(2, end); // end-1??
			rs = pstmt.executeQuery();
			if (rs.next()) {
				articleList = new ArrayList(end);
				do {
					HBoardDTO article = this.makeArticleFromResult();
					articleList.add(article); // ������ ����
				} while (rs.next());
			}
		} catch (Exception e) {
			System.out.println("getBoardArticles() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return articleList;
	}

	// �۸����ȸ-�˻����, h_ref ����
	public List<HBoardDTO> getBoardArticles(int start, int end, String search, String searchtext, String h_ref) {

		List<HBoardDTO> articleList = null;

		try {
			con = pool.getConnection();

			if (search == null || search == "" || h_ref == "") {
				sql = "select * from (select h_table.*, rownum as rn from (select * from h_table where h_ref=" + h_ref
						+ " order by h_number desc) h_table) where rn >= ? and rn <= ?";
			} else { // ����+����
				if (search.equals("subject_content")) { // ����+����
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table "
							+ "where h_title like '%" + searchtext + "%' or h_body like '%" + searchtext
							+ "%') h_table) where rn >= ? and rn <= ?";
				} else {
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table " + " where "
							+ search + " like '%" + searchtext + "%') h_table) where rn >= ? and rn <= ?";
				}
			}
			System.out.println("getBoardArticles()h_ref �� sql=>" + sql);
			// -------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start); // mysql�� start-1
			pstmt.setInt(2, end); // end-1??
			rs = pstmt.executeQuery();
			if (rs.next()) {
				articleList = new ArrayList(end);
				do {
					HBoardDTO article = this.makeArticleFromResult();
					articleList.add(article); // ������ ����
				} while (rs.next());
			}
		} catch (Exception e) {
			System.out.println("getBoardArticles()h_ref ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return articleList;
	}

	// �۸����ȸ-�˻���� ����
	public List<HBoardDTO> getHotBoardArticles(int start, int end, String search, String searchtext) {

		List<HBoardDTO> articleList = null;

		try {
			con = pool.getConnection();

			if (search == null || search == "") {
				sql = "select * from (select h_table.*, rownum as rn from (select * from h_table where h_count > 50 order by h_number desc) h_table) where rn >= ? and rn <= ?";
			} else { // ����+����
				if (search.equals("subject_content")) { // ����+����
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table "
							+ "where h_count > 50 and (h_title like '%" + searchtext + "%' or h_body like '%"
							+ searchtext + "%')) h_table) where rn >= ? and rn <= ?";
				} else {
					sql = "select * from (select h_table.*, rownum as rn from (select * from h_table "
							+ " where h_count > 50 " + search + " like '%" + searchtext
							+ "%') h_table) where rn >= ? and rn <= ?";
				}
			}
			System.out.println("getHotBoardArticles()�� sql=>" + sql);
			// -------------------------------------------------------
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start); // mysql�� start-1
			pstmt.setInt(2, end); // end-1??
			rs = pstmt.executeQuery();
			if (rs.next()) {
				articleList = new ArrayList(end);
				do {
					HBoardDTO article = this.makeArticleFromResult();
					articleList.add(article); // ������ ����
				} while (rs.next());
			}
		} catch (Exception e) {
			System.out.println("getBoardArticles() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return articleList;
	}

	// ����¡ ó�� ���
	public Hashtable pageList(String pageNum, int count) {

		Hashtable<String, Integer> pgList = new Hashtable<String, Integer>();

		int pageSize = 10; // numPerPage=>�������� �����ִ� �Խù� ��(=���ڵ� ��) default:10
		int blockSize = 10; // pagePerBlock=>���� �����ִ� �������� default:10

		// �Խ����� �� ó�� �����Ű�� ������ 1���������� ���->���� �ֱ��� ���� ������ ����
		if (pageNum == null) {
			pageNum = "1"; // default(������ 1�������� �������� �ʾƵ� ������� �ϱ� ����)
		}
		int currentPage = Integer.parseInt(pageNum); // "1"->1(=nowPage)(���� ������)
		// (1-1)*10+1=1, (2-1)*10+1=11, (3-1)*10+1=21
		int startRow = (currentPage - 1) * pageSize + 1; // ���� ���ڵ� ��ȣ
		int endRow = currentPage * pageSize; // 1*10=10, 2*10=20, 3*10=30
		int number = 0; // beginPerPage->���������� �����ϴ� �� ó���� ������ �Խù���ȣ
		System.out.println("���� ���ڵ� ��(count)=>" + count);
		number = count - (currentPage - 1) * pageSize;
		System.out.println(currentPage + "�������� number=>" + number);
		// ��1�� list.jsp ����¡ ó�� ����
		// 122/10=12.2+1.0=13.2=13
		int pageCount = count / pageSize + (count % pageSize == 0 ? 0 : 1);
		// 2. ���������� @@@@�߿�@@@@
		int startPage = 0;
		if (currentPage % blockSize != 0) { // ������ ���� 1~9, 11~19. 21~29 (10�� ����� �ƴ� ���)
			startPage = currentPage / blockSize * blockSize + 1; // ��輱 ������
		} else { // 10%10=0(10,20,30,40,,,)
					// ((10/10)-1)*10+1=1
			startPage = ((currentPage / blockSize) - 1) * blockSize + 1;
		}
		// ����������
		int endPage = startPage + blockSize - 1; // 1+10-1=10, 2+10-1=11
		System.out.println("startPage=>" + startPage + ", endPage=>" + endPage);
		// ������ �����ؼ� ��ũ �ɾ ���(������ ������ > �� ������ �� �� �� ��... ���ƾ� ��)
		// 11>10=>endPage=10
		if (endPage > pageCount)
			endPage = pageCount; // ������ ������=�� ������ �� �̵��� ������ֱ�
		// ����¡ ó���� ���� �����->Hashtable, HashMap=>ListAction�� ����->list.jsp
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

	// �۾���
	public void insertArticle(HBoardDTO article) {

		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int h_number = article.getH_number();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertArticle()�޼��� ���� h_number=>" + h_number);

		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(h_number) from h_table";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				number = rs.getInt(1) + 1; // �ִ밪+1
				System.out.println("�� ��ȣ(number)=>" + number);
			} else {
				number = 1;
			}
			con.commit();

			sql = "insert into h_table(h_number, mem_id, h_nickname, h_title, h_body, h_date, h_category, h_ref) ";
			sql += " values(?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, article.getMem_id());
			pstmt.setString(3, article.getH_nickname());
			pstmt.setString(4, article.getH_title());
			pstmt.setString(5, article.getH_body());
			pstmt.setTimestamp(6, article.getH_date());
			pstmt.setInt(7, article.getH_category());
			pstmt.setInt(8, article.getH_ref());

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

	// �� ���� ��ȸ
	public HBoardDTO getArticle(int num) {

		HBoardDTO article = null;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);

			sql = "update h_table set h_count=h_count+1 where h_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			int update = pstmt.executeUpdate();
			con.commit();
			System.out.println("��ȸ�� ��������(update)=>" + update + " //(1���� 0����)");

			sql = "select * from h_table where h_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();

			if (rs.next()) { // �����ִ� ����� �ִٸ�
				article = this.makeArticleFromResult();
				con.commit();
			}
		} catch (Exception e) {
			System.out.println("getArticle() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return article;
	}

	// �ۼ���-������ ������ ã��
	public HBoardDTO updateGetArticle(int num) {

		HBoardDTO article = null;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select * from h_table where h_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				article = makeArticleFromResult();
				con.commit();
			}
		} catch (Exception e) {
			System.out.println("updateGetArticle() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return article;
	}

	// �ۼ���
	public int updateArticle(HBoardDTO article) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "update h_table set h_title=?, h_body=?, h_category=?, h_ref=? where h_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, article.getH_title());
			pstmt.setString(2, article.getH_body());
			pstmt.setInt(3, article.getH_category());
			pstmt.setInt(4, article.getH_ref());
			pstmt.setInt(5, article.getH_number());

			int update = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۼ��� ���� ����(update)=>" + update + " //(1���� 0����)");
			x = 1; // ��������

		} catch (Exception e) {
			System.out.println("updateArticle() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

	// �ۻ���
	public int deleteArticle(int h_number, String h_title, String h_mem_id) {
		// String dbpasswd="";
		int x = -1;
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from h_table where h_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, h_number);

			int delete1 = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� ��������=>" + delete1 + " //(1���� 0����)");

			String sql2 = "delete from h_comment where h_number=?";
			pstmt = con.prepareStatement(sql2);
			pstmt.setInt(1, h_number);

			int delete2 = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� �� ��� ���� ��������=>" + delete2 + " //(1���� 0����)");

			String sql3 = "delete from scrap where s_number=? and s_title=?";
			pstmt = con.prepareStatement(sql3);
			pstmt.setInt(1, h_number);
			pstmt.setString(2, h_title);

			int delete3 = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� �� ��ũ�� ���� ��������=>" + delete3 + " //(1���� 0����)");

			if (delete1 > 0) {
				String sql4 = "update login set log_point=log_point-10 where mem_id=?";
				pstmt = con.prepareStatement(sql4);
				pstmt.setString(1, h_mem_id);

				int pointdown = pstmt.executeUpdate();
				con.commit();
				System.out.println("�� ������ ����Ʈ ȸ������(pointdown)=>" + pointdown);

				if (pointdown > 0) {
					String sql5 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql5);
					pstmt.setString(1, h_mem_id);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>" + point + ", ���(grade)=>" + grade);

						if (point < 100) {
							sql = "update login set log_grade=1 where mem_id=?";
						} else if (point >= 100 && point < 500) {
							sql = "update login set log_grade=2 where mem_id=?";
						} else if (point >= 500 && point < 2000) {
							sql = "update login set log_grade=3 where mem_id=?";
						} else if (point >= 2000 && point < 10000) {
							sql = "update login set log_grade=4 where mem_id=?";
						} else if (point >= 10000) {
							sql = "update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql=>" + sql);
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, h_mem_id);

						int gradeup = pstmt.executeUpdate();
						con.commit();
						System.out.println("�� ���� �� ��� ��������(gradeup)=>" + gradeup);
					}
				}
			}
			x = 1;
		} catch (Exception e) {
			System.out.println("deleteArticle() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

}
