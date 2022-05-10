package beans;

import java.sql.*;
import java.util.*;

public class GCommentDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	public GCommentDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	private GCommentDTO makeArticleFromResult() throws Exception {

		GCommentDTO article = new GCommentDTO();

		article.setG_cnumber(rs.getInt("g_cnumber"));
		article.setMem_id(rs.getString("mem_id"));
		article.setG_number(rs.getInt("g_number"));
		article.setG_cbody(rs.getString("g_cbody"));
		article.setG_cnickname(rs.getString("g_cnickname"));
		article.setG_cdate(rs.getTimestamp("g_cdate"));
		article.setG_cref(rs.getInt("g_cref"));
		article.setG_cre_step(rs.getInt("g_cre_step"));
		article.setG_cre_level(rs.getInt("g_cre_level"));

		return article;
	}

	public List<GCommentDTO> getComment(int g_number) {

		List<GCommentDTO> commentList = new ArrayList<GCommentDTO>();

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select * from g_comment where g_number=? order by g_cref asc, g_cre_step asc, g_cdate asc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, g_number);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					GCommentDTO comment = new GCommentDTO();
					comment = this.makeArticleFromResult();
					commentList.add(comment);
					con.commit();
				} while (rs.next());
			}
			con.commit();
		} catch (Exception e) {
			System.out.println("getComment() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return commentList;
	}


	// �Խ����� �۾��� �� �亯�� ����

	public void insertComment(GCommentDTO comment) {
		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int g_cnumber = comment.getG_cnumber();
		int g_cref = comment.getG_cref();
		int g_cre_step = comment.getG_cre_step();
		int g_cre_level = comment.getG_cre_level();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertComment()�޼��� ���� g_cnumber=>" + g_cnumber);
		System.out.println("g_cref=>" + g_cref + ", g_cre_step=>" + g_cre_step + ", g_cre_level=>" + g_cre_level);
		
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(g_cnumber) from g_comment";
			// System.out.println("insertComment()�� ù ��° sql=>"+sql);
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				number = rs.getInt(1) + 1; // �ִ밪+1
				System.out.println("��� ��ȣ(number)=>" + number);
			} else {
				number = 1;
			}
			con.commit();
			// �亯���̶��(�Խñ۹�ȣ�� ����̸鼭 1 �̻��� ���)
			if (g_cnumber != 0) {
				// ���� �׷��ȣ�� ������ �����鼭(ref=?) ������ step���� ū �Խù�(re_step>?)�� ã�Ƽ� �� step �ϳ� ����
				// @@----->�������ѳ��� �ڱⰡ 1�� �ż� ������ ����

				System.out.println("update ���� g_cre_step=>" + g_cre_step);

				// g_cref�� ���� ��۵� �� step���� ���� ���� �� ã�Ƽ� �װͺ��� 1 ũ�� ����� ����
				String sql2 = "select max(g_cre_step) from g_comment where g_cref=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setInt(1, g_cref);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					g_cre_step = rs.getInt(1) + 1;
					System.out.println("g_cre_step�� �ִ�+1=>" + g_cre_step);
				} else {
					g_cre_step = g_cre_step + 1;
				}
				g_cre_level = g_cre_level + 1;
				System.out.println("update ���� g_cre_step=>" + g_cre_step + ", g_cre_level=>" + g_cre_level);
				con.commit();

				/*
				 * sql="update g_comment set g_cre_step=g_cre_step+1 where g_cref=? and g_cre_step>?"
				 * ;
				 * 
				 * System.out.println("insertComment()�� sql=>"+sql);
				 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, g_cref); pstmt.setInt(2,
				 * g_cre_step); int update=pstmt.executeUpdate(); con.commit();
				 * System.out.println("��ۼ�������(update)=>"+update+" //(1���� 0����)"); //1���� 0����
				 * System.out.println("update �ϰ� ������ g_cre_step()=>"+g_cre_step);
				 */
				// �亯��
				// g_cre_step=g_cre_step + 1;

			} else { // �űԱ��̶�� num=0
				g_cref = number;
				g_cre_step = 0;
				g_cre_level = 0;
				con.commit();
			}
			sql = "insert into g_comment(g_cnumber, mem_id, g_number, g_cbody, g_cnickname, g_cdate, ";
			sql += " g_cref, g_cre_step, g_cre_level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, comment.getMem_id());
			pstmt.setInt(3, comment.getG_number());
			pstmt.setString(4, comment.getG_cbody());
			pstmt.setString(5, comment.getG_cnickname());
			pstmt.setTimestamp(6, comment.getG_cdate());

			pstmt.setInt(7, g_cref);
			pstmt.setInt(8, g_cre_step);
			pstmt.setInt(9, g_cre_level);

			int insert = pstmt.executeUpdate();
			con.commit();
			System.out.println("��۾��� ��������(insert)=>" + insert + " //(1���� 0����)");
			
			if (insert > 0) {
				String sql3="update login set log_point=log_point+5 where mem_id=?";
				pstmt=con.prepareStatement(sql3);
				pstmt.setString(1, comment.getMem_id());
				
				int pointup=pstmt.executeUpdate();
				con.commit();
				System.out.println("��� �ۼ� ����Ʈ ��������(pointup)=>"+pointup);
				
				if (pointup > 0) {
					String sql4 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql4);
					pstmt.setString(1, comment.getMem_id());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>"+point+", ���(grade)=>"+grade);
						
						String sql5="";
						if (point < 100) {
							sql5="update login set log_grade=1 where mem_id=?";
						}else if (point >= 100 && point < 500) {
							sql5="update login set log_grade=2 where mem_id=?";
						}else if (point >= 500 && point < 2000) {
							sql5="update login set log_grade=3 where mem_id=?";
						}else if (point >= 2000 && point < 10000) {
							sql5="update login set log_grade=4 where mem_id=?";
						}else if (point >= 10000) {
							sql5="update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql5=>"+sql5);
						pstmt=con.prepareStatement(sql5);
						pstmt.setString(1, comment.getMem_id());
							
						int gradeup=pstmt.executeUpdate();
						con.commit();
						System.out.println("��� �ۼ� �� ��� ��������(gradeup)=>"+gradeup);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("insertComment()�޼��� ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
	}

	// ��� ����

	// ��� ����->���̺��� �����ϴ� �� �ƴ϶� '������ ����Դϴ�' �� ����@@@
	public int deleteComment(int g_cnumber) {

		int x = -1;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from g_comment where g_cnumber=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, g_cnumber);

			int delete = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ��� ��������=>" + delete + " //(1���� 0����)");
			x = 1;

		} catch (Exception e) {
			System.out.println("deleteComment() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

	// �ۻ���->������ ����Դϴ�
	public int upDeleteComment(int g_cnumber, int g_cref, int g_cre_level) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			String sql1="select * from g_comment where g_cref=? and g_cre_level>?";
			pstmt=con.prepareStatement(sql1);
			pstmt.setInt(1, g_cref);
			pstmt.setInt(2, g_cre_level);
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //���۵��� �ִ� ����̶��
				sql = "update g_comment set g_cnickname=' ', g_cbody='������ ����Դϴ�.&nbsp;' where g_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, g_cnumber);
			}else {
				sql = "delete from g_comment where g_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, g_cnumber);
			}
			
			int update = pstmt.executeUpdate();
			con.commit();
			System.out.println("�Խ��� �ۻ���(����) ���� ����(update)=>" + update + " //(1���� 0����)");
			x = 1; // ��������
		} catch (Exception e) {
			System.out.println("upDeleteComment() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

	// ��� ��
	public int getCommentCount(int g_number) { // getmemberCount()->MemberDAO���� �ۼ�
		int x = 0;
		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from g_comment where g_number=? and g_cbody!='������ ����Դϴ�.&nbsp;'";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, g_number);
			rs = pstmt.executeQuery();
			if (rs.next()) { // �����ִ� ����� �ִٸ�
				x = rs.getInt(1); // ������=rs.get�ڷ���(�ʵ�� �Ǵ� �ε�����ȣ)=>���⼭�� �ʵ��X(�ʵ���� ���� ����)
			}
		} catch (Exception e) {
			System.out.println("getCommentCount() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

}
