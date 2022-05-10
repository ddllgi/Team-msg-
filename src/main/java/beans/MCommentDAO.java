package beans;

import java.sql.*;
import java.util.*;

public class MCommentDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	public MCommentDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	private MCommentDTO makeArticleFromResult() throws Exception {

		MCommentDTO article = new MCommentDTO();

		article.setM_cnumber(rs.getInt("m_cnumber"));
		article.setMem_id(rs.getString("mem_id"));
		article.setM_number(rs.getInt("m_number"));
		article.setM_cbody(rs.getString("m_cbody"));
		article.setM_cnickname(rs.getString("m_cnickname"));
		article.setM_cdate(rs.getTimestamp("m_cdate"));
		article.setM_cref(rs.getInt("m_cref"));
		article.setM_cre_step(rs.getInt("m_cre_step"));
		article.setM_cre_level(rs.getInt("m_cre_level"));

		return article;
	}

	public List<MCommentDTO> getComment(int m_number) {

		List<MCommentDTO> commentList = new ArrayList<MCommentDTO>();

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select * from m_comment where m_number=? order by m_cref asc, m_cre_step asc, m_cdate asc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, m_number);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					MCommentDTO comment = new MCommentDTO();
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

	public void insertComment(MCommentDTO comment) {
		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int m_cnumber = comment.getM_cnumber();
		int m_cref = comment.getM_cref();
		int m_cre_step = comment.getM_cre_step();
		int m_cre_level = comment.getM_cre_level();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertComment()�޼��� ���� m_cnumber=>" + m_cnumber);
		System.out.println("m_cref=>" + m_cref + ", m_cre_step=>" + m_cre_step + ", m_cre_level=>" + m_cre_level);
		
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(m_cnumber) from m_comment";
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
			if (m_cnumber != 0) {
				// ���� �׷��ȣ�� ������ �����鼭(ref=?) ������ step���� ū �Խù�(re_step>?)�� ã�Ƽ� �� step �ϳ� ����
				// @@----->�������ѳ��� �ڱⰡ 1�� �ż� ������ ����

				System.out.println("update ���� m_cre_step=>" + m_cre_step);

				// m_cref�� ���� ��۵� �� step���� ���� ���� �� ã�Ƽ� �װͺ��� 1 ũ�� ����� ����
				String sql2 = "select max(m_cre_step) from m_comment where m_cref=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setInt(1, m_cref);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					m_cre_step = rs.getInt(1) + 1;
					System.out.println("m_cre_step�� �ִ�+1=>" + m_cre_step);
				} else {
					m_cre_step = m_cre_step + 1;
				}
				m_cre_level = m_cre_level + 1;
				System.out.println("update ���� m_cre_step=>" + m_cre_step + ", m_cre_level=>" + m_cre_level);
				con.commit();

				/*
				 * sql="update m_comment set m_cre_step=m_cre_step+1 where m_cref=? and m_cre_step>?"
				 * ;
				 * 
				 * System.out.println("insertComment()�� sql=>"+sql);
				 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, m_cref); pstmt.setInt(2,
				 * m_cre_step); int update=pstmt.executeUpdate(); con.commit();
				 * System.out.println("��ۼ�������(update)=>"+update+" //(1���� 0����)"); //1���� 0����
				 * System.out.println("update �ϰ� ������ m_cre_step()=>"+m_cre_step);
				 */
				// �亯��
				// m_cre_step=m_cre_step + 1;

			} else { // �űԱ��̶�� num=0
				m_cref = number;
				m_cre_step = 0;
				m_cre_level = 0;
				con.commit();
			}
			sql = "insert into m_comment(m_cnumber, mem_id, m_number, m_cbody, m_cnickname, m_cdate, ";
			sql += " m_cref, m_cre_step, m_cre_level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, comment.getMem_id());
			pstmt.setInt(3, comment.getM_number());
			pstmt.setString(4, comment.getM_cbody());
			pstmt.setString(5, comment.getM_cnickname());
			pstmt.setTimestamp(6, comment.getM_cdate());

			pstmt.setInt(7, m_cref);
			pstmt.setInt(8, m_cre_step);
			pstmt.setInt(9, m_cre_level);

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
	public int deleteComment(int m_cnumber) {

		int x = -1;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from m_comment where m_cnumber=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, m_cnumber);

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
	public int upDeleteComment(int m_cnumber, int m_cref, int m_cre_level) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			String sql1="select * from m_comment where m_cref=? and m_cre_level>?";
			pstmt=con.prepareStatement(sql1);
			pstmt.setInt(1, m_cref);
			pstmt.setInt(2, m_cre_level);
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //���۵��� �ִ� ����̶��
				sql = "update m_comment set m_cnickname=' ', m_cbody='������ ����Դϴ�.&nbsp;' where m_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, m_cnumber);
			}else {
				sql = "delete from m_comment where m_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, m_cnumber);
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
	public int getCommentCount(int m_number) { // getmemberCount()->MemberDAO���� �ۼ�
		int x = 0;
		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from m_comment where m_number=? and m_cbody!='������ ����Դϴ�.&nbsp;'";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, m_number);
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
