package beans;

import java.sql.*;
import java.util.*;
import beans.*;

public class NoticeDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	// 2. ������ ���ؼ� ����
	public NoticeDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	// article~
	private NoticeDTO makeArticleFromResult() throws Exception {

		NoticeDTO article = new NoticeDTO();

		article.setNotice_number(rs.getInt("notice_number"));
		article.setAdmin_id(rs.getString("admin_id"));
		article.setNot_title(rs.getString("not_title"));
		article.setNot_body(rs.getString("not_body"));
		article.setNot_date(rs.getTimestamp("not_date"));
		article.setNot_count(rs.getInt("not_count")); // ��ȸ��

		return article;
	}

	// �޼��� �ۼ�~

	// �� ���ڵ�(�Խù�) ��
	public int getArticleCount() {

		int x = 0;

		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from notice";
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
				sql = "select count(*) from notice";
			} else { // �˻��о�(����, �ۼ���, ����+����)
				if (search.equals("not_title_not_body")) { // ����+����
					sql = "select count(*) from notice where not_title like '%" + searchtext + "%' or not_body like '%"
							+ searchtext + "%' ";
				} else { // ����, �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ����
					sql = "select count(*) from notice where " + search + " like '%" + searchtext + "%' ";
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

	/*
	 * // �˻�� �ش�Ǵ� �� ���ڵ� �� ref public int getArticleSearchCount(String search,
	 * String searchtext, String h_ref) {
	 * 
	 * int x = 0;
	 * 
	 * try { con = pool.getConnection(); System.out.println("con=>" + con); //
	 * -------------------------------------------------------------------------- if
	 * (search == null || search == "") { // �˻��о߸� ����X sql =
	 * "select count(*) from h_table where h_ref=" + h_ref; } else { // �˻��о�(����,
	 * �ۼ���, ����+����) if (search.equals("h_title_h_body")) { // ����+���� sql =
	 * "select count(*) from h_table where h_ref=" + h_ref + " and h_title like '%"
	 * + searchtext + "%' or h_body like '%" + searchtext + "%' "; } else { // ����,
	 * �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ���� sql =
	 * "select count(*) from h_table where h_ref=" + h_ref + " and " + search +
	 * " like '%" + searchtext + "%' "; } }
	 * System.out.println("getArticleSearchCount()h_ref �˻���=>" + sql); //
	 * --------------------------------------------------------------------------
	 * pstmt = con.prepareStatement(sql); rs = pstmt.executeQuery();
	 * 
	 * if (rs.next()) { x = rs.getInt(1); System.out.println("�˻��� �� ���ڵ� ��=>" + x); }
	 * } catch (Exception e) {
	 * System.out.println("getArticleSearchCount()h_ref ���� �߻�=>" + e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * return x; }
	 * 
	 * // �˻�� �ش�Ǵ� �� ���ڵ� �� HOT public int getHotArticleSearchCount(String search,
	 * String searchtext) {
	 * 
	 * int x = 0;
	 * 
	 * try { con = pool.getConnection(); System.out.println("con=>" + con); //
	 * -------------------------------------------------------------------------- if
	 * (search == null || search == "") { // �˻��о߸� ����X sql =
	 * "select count(*) from h_table where h_count > 50"; } else { // �˻��о�(����, �ۼ���,
	 * ����+����) if (search.equals("h_title_h_body")) { // ����+���� sql =
	 * "select count(*) from h_table where h_count > 50 and (h_title like '%" +
	 * searchtext + "%' or h_body like '%" + searchtext + "%') "; } else { // ����,
	 * �ۼ���->�Ű������� �̿��ؼ� �ϳ��� sql�� ���� ���� sql =
	 * "select count(*) from h_table where h_count > 50 and " + search + " like '%"
	 * + searchtext + "%' "; } } System.out.println("getArticleSearchCount �˻���=>" +
	 * sql); //
	 * --------------------------------------------------------------------------
	 * pstmt = con.prepareStatement(sql); rs = pstmt.executeQuery();
	 * 
	 * if (rs.next()) { x = rs.getInt(1); System.out.println("�˻��� �� ���ڵ� ��=>" + x); }
	 * } catch (Exception e) {
	 * System.out.println("getHotArticleSearchCount() ���� �߻�=>" + e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * return x; }
	 */

	// �۸����ȸ
	public List<NoticeDTO> getArticles(int start, int end) {

		List<NoticeDTO> articleList = null;

		try {
			con = pool.getConnection();
			sql = "select * from (select notice.*, rownum as rn from (select * from notice order by notice_number desc) notice) where rn between ? and (? - 1)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, start + end);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				articleList = new ArrayList(end);
				do {
					NoticeDTO article = this.makeArticleFromResult();
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

	// �۸����ȸ-�˻���� ����
	public List<NoticeDTO> getBoardArticles(int start, int end, String search, String searchtext) {

		List<NoticeDTO> articleList = null;

		try {
			con = pool.getConnection();

			if (search == null || search == "") {
				sql = "select * from (select notice.*, rownum as rn from (select * from notice order by notice_number desc) notice) where rn >= ? and rn <= ?";
			} else { // ����+����
				if (search.equals("not_title_not_body")) { // ����+����
					sql = "select * from (select notice.*, rownum as rn from (select * from notice "
							+ "where not_title like '%" + searchtext + "%' or not_body like '%" + searchtext
							+ "%') notice) where rn >= ? and rn <= ?";
				} else {
					sql = "select * from (select notice.*, rownum as rn from (select * from notice " + " where "
							+ search + " like '%" + searchtext + "%') notice) where rn >= ? and rn <= ?";
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
					NoticeDTO article = this.makeArticleFromResult();
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

	/*
	 * // �۸����ȸ-�˻����, h_ref ���� public List<HBoardDTO> getBoardArticles(int start,
	 * int end, String search, String searchtext, String h_ref) {
	 * 
	 * List<HBoardDTO> articleList = null;
	 * 
	 * try { con = pool.getConnection();
	 * 
	 * if (search == null || search == "" || h_ref == "") { sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table where h_ref="
	 * + h_ref + " order by h_number desc) h_table) where rn >= ? and rn <= ?"; }
	 * else { // ����+���� if (search.equals("subject_content")) { // ����+���� sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table "
	 * + "where h_title like '%" + searchtext + "%' or h_body like '%" + searchtext
	 * + "%') h_table) where rn >= ? and rn <= ?"; } else { sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table "
	 * + " where " + search + " like '%" + searchtext +
	 * "%') h_table) where rn >= ? and rn <= ?"; } }
	 * System.out.println("getBoardArticles()h_ref �� sql=>" + sql); //
	 * ------------------------------------------------------- pstmt =
	 * con.prepareStatement(sql); pstmt.setInt(1, start); // mysql�� start-1
	 * pstmt.setInt(2, end); // end-1?? rs = pstmt.executeQuery(); if (rs.next()) {
	 * articleList = new ArrayList(end); do { HBoardDTO article =
	 * this.makeArticleFromResult(); articleList.add(article); // ������ ���� } while
	 * (rs.next()); } } catch (Exception e) {
	 * System.out.println("getBoardArticles()h_ref ���� �߻�=>" + e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * return articleList; }
	 * 
	 * // �۸����ȸ-�˻���� ���� public List<HBoardDTO> getHotBoardArticles(int start, int
	 * end, String search, String searchtext) {
	 * 
	 * List<HBoardDTO> articleList = null;
	 * 
	 * try { con = pool.getConnection();
	 * 
	 * if (search == null || search == "") { sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table where h_count > 50 order by h_number desc) h_table) where rn >= ? and rn <= ?"
	 * ; } else { // ����+���� if (search.equals("subject_content")) { // ����+���� sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table "
	 * + "where h_count > 50 and (h_title like '%" + searchtext +
	 * "%' or h_body like '%" + searchtext +
	 * "%')) h_table) where rn >= ? and rn <= ?"; } else { sql =
	 * "select * from (select h_table.*, rownum as rn from (select * from h_table "
	 * + " where h_count > 50 " + search + " like '%" + searchtext +
	 * "%') h_table) where rn >= ? and rn <= ?"; } }
	 * System.out.println("getHotBoardArticles()�� sql=>" + sql); //
	 * ------------------------------------------------------- pstmt =
	 * con.prepareStatement(sql); pstmt.setInt(1, start); // mysql�� start-1
	 * pstmt.setInt(2, end); // end-1?? rs = pstmt.executeQuery(); if (rs.next()) {
	 * articleList = new ArrayList(end); do { HBoardDTO article =
	 * this.makeArticleFromResult(); articleList.add(article); // ������ ���� } while
	 * (rs.next()); } } catch (Exception e) {
	 * System.out.println("getBoardArticles() ���� �߻�=>" + e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * return articleList; }
	 */

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
	public void insertArticle(NoticeDTO article) {

		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int notice_number = article.getNot_count();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertArticle()�޼��� ���� notice_number=>" + notice_number);

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(notice_number) from notice";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				number = rs.getInt(1) + 1; // �ִ밪+1
				System.out.println("�� ��ȣ(number)=>" + number);
			} else {
				number = 1;
			}
			con.commit();

			sql = "insert into notice(notice_number, admin_id, not_title, not_body, not_date) ";
			sql += " values(?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, article.getAdmin_id());
			pstmt.setString(3, article.getNot_title());
			pstmt.setString(4, article.getNot_body());
			pstmt.setTimestamp(5, article.getNot_date());

			int insert = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �۾��� ��������(insert)=>" + insert + " //(1���� 0����)");
		} catch (Exception e) {
			System.out.println("insertArticle()�޼��� ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

	}

	// �� ���� ��ȸ
	public NoticeDTO getArticle(int num) {

		NoticeDTO article = null;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);

			sql = "update notice set not_count=not_count+1 where notice_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			int update = pstmt.executeUpdate();
			con.commit();
			System.out.println("��ȸ�� ��������(update)=>" + update + " //(1���� 0����)");

			sql = "select * from notice where notice_number=?";
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
	public NoticeDTO updateGetArticle(int num) {

		NoticeDTO article = null;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select * from notice where notice_number=?";
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
	public int updateArticle(NoticeDTO article) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			sql = "update notice set not_title=?, not_body=? where notice_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, article.getNot_title());
			pstmt.setString(2, article.getNot_body());
			pstmt.setInt(3, article.getNotice_number());

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
	public int deleteArticle(int num) {
		// String dbpasswd="";
		int x = -1;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from notice where notice_number=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			int delete = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� ��������=>" + delete + " //(1���� 0����)");
			x = 1;
			
		} catch (Exception e) {
			System.out.println("deleteArticle() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

}
