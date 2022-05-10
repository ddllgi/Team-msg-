package beans;

import java.sql.*;
import java.util.*;

public class RCommentDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	public RCommentDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	private RCommentDTO makeArticleFromResult() throws Exception {

		RCommentDTO article = new RCommentDTO();

		article.setR_cnumber(rs.getInt("r_cnumber"));
		article.setMem_id(rs.getString("mem_id"));
		article.setR_number(rs.getInt("r_number"));
		article.setR_cbody(rs.getString("r_cbody"));
		article.setR_cnickname(rs.getString("r_cnickname"));
		article.setR_cdate(rs.getTimestamp("r_cdate"));
		article.setR_cref(rs.getInt("r_cref"));
		article.setR_cre_step(rs.getInt("r_cre_step"));
		article.setR_cre_level(rs.getInt("r_cre_level"));

		return article;
	}

	public List<RCommentDTO> getComment(int r_number) {

		List<RCommentDTO> commentList = new ArrayList<RCommentDTO>();

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select * from r_comment where r_number=? order by r_cref asc, r_cre_step asc, r_cdate asc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, r_number);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					RCommentDTO comment = new RCommentDTO();
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

	public void insertComment(RCommentDTO comment) {
		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int r_cnumber = comment.getR_cnumber();
		int r_cref = comment.getR_cref();
		int r_cre_step = comment.getR_cre_step();
		int r_cre_level = comment.getR_cre_level();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertComment()�޼��� ���� r_cnumber=>" + r_cnumber);
		System.out.println("r_cref=>" + r_cref + ", r_cre_step=>" + r_cre_step + ", r_cre_level=>" + r_cre_level);
		
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(r_cnumber) from r_comment";
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
			if (r_cnumber != 0) {
				// ���� �׷��ȣ�� ������ �����鼭(ref=?) ������ step���� ū �Խù�(re_step>?)�� ã�Ƽ� �� step �ϳ� ����
				// @@----->�������ѳ��� �ڱⰡ 1�� �ż� ������ ����

				System.out.println("update ���� r_cre_step=>" + r_cre_step);

				// r_cref�� ���� ��۵� �� step���� ���� ���� �� ã�Ƽ� �װͺ��� 1 ũ�� ����� ����
				String sql2 = "select max(r_cre_step) from r_comment where r_cref=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setInt(1, r_cref);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					r_cre_step = rs.getInt(1) + 1;
					System.out.println("r_cre_step�� �ִ�+1=>" + r_cre_step);
				} else {
					r_cre_step = r_cre_step + 1;
				}
				r_cre_level = r_cre_level + 1;
				System.out.println("update ���� r_cre_step=>" + r_cre_step + ", r_cre_level=>" + r_cre_level);
				con.commit();

				/*
				 * sql="update r_comment set r_cre_step=r_cre_step+1 where r_cref=? and r_cre_step>?"
				 * ;
				 * 
				 * System.out.println("insertComment()�� sql=>"+sql);
				 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, r_cref); pstmt.setInt(2,
				 * r_cre_step); int update=pstmt.executeUpdate(); con.commit();
				 * System.out.println("��ۼ�������(update)=>"+update+" //(1���� 0����)"); //1���� 0����
				 * System.out.println("update �ϰ� ������ r_cre_step()=>"+r_cre_step);
				 */
				// �亯��
				// r_cre_step=r_cre_step + 1;

			} else { // �űԱ��̶�� num=0
				r_cref = number;
				r_cre_step = 0;
				r_cre_level = 0;
				con.commit();
			}
			sql = "insert into r_comment(r_cnumber, mem_id, r_number, r_cbody, r_cnickname, r_cdate, ";
			sql += " r_cref, r_cre_step, r_cre_level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, comment.getMem_id());
			pstmt.setInt(3, comment.getR_number());
			pstmt.setString(4, comment.getR_cbody());
			pstmt.setString(5, comment.getR_cnickname());
			pstmt.setTimestamp(6, comment.getR_cdate());

			pstmt.setInt(7, r_cref);
			pstmt.setInt(8, r_cre_step);
			pstmt.setInt(9, r_cre_level);

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
	public int deleteComment(int r_cnumber) {

		int x = -1;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from r_comment where r_cnumber=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, r_cnumber);

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
	public int upDeleteComment(int r_cnumber, int r_cref, int r_cre_level) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			String sql1="select * from r_comment where r_cref=? and r_cre_level>?";
			pstmt=con.prepareStatement(sql1);
			pstmt.setInt(1, r_cref);
			pstmt.setInt(2, r_cre_level);
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //���۵��� �ִ� ����̶��
				sql = "update r_comment set r_cnickname=' ', r_cbody='������ ����Դϴ�.&nbsp;' where r_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, r_cnumber);
			}else {
				sql = "delete from r_comment where r_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, r_cnumber);
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
	public int getCommentCount(int r_number) { // getmemberCount()->MemberDAO���� �ۼ�
		int x = 0;
		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from r_comment where r_number=? and r_cbody!='������ ����Դϴ�.&nbsp;'";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, r_number);
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
