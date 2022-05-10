package beans;

import java.sql.*;
import java.util.*;

public class ZCommentDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	public ZCommentDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	private ZCommentDTO makeArticleFromResult() throws Exception {

		ZCommentDTO article = new ZCommentDTO();

		article.setZ_cnumber(rs.getInt("z_cnumber"));
		article.setMem_id(rs.getString("mem_id"));
		article.setZ_number(rs.getInt("z_number"));
		article.setZ_cbody(rs.getString("z_cbody"));
		article.setZ_cnickname(rs.getString("z_cnickname"));
		article.setZ_cdate(rs.getTimestamp("z_cdate"));
		article.setZ_cref(rs.getInt("z_cref"));
		article.setZ_cre_step(rs.getInt("z_cre_step"));
		article.setZ_cre_level(rs.getInt("z_cre_level"));

		return article;
	}

	public List<ZCommentDTO> getComment(int z_number) {

		List<ZCommentDTO> commentList = new ArrayList<ZCommentDTO>();

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select * from z_comment where z_number=? order by z_cref asc, z_cre_step asc, z_cdate asc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, z_number);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					ZCommentDTO comment = new ZCommentDTO();
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

	/*
	 * public void insertComment(ZCommentDTO comment) {
	 * 
	 * int z_cnumber = comment.getZ_cnumber(); int result = 0; int number = 0;
	 * String sql1 = "select max(z_cnumber) from z_comment"; sql =
	 * "insert into z_comment values(?, ?, ?, ?, ?, ?, ?, ?, ?)"; String sql2 =
	 * "update z_comment set z_cre_step=z_cre_step+1 where z_cref=? and z_cre_step>?"
	 * ; try { con = pool.getConnection(); con.setAutoCommit(false);
	 * System.out.println("con=>" + con);
	 * 
	 * if (z_cnumber != 0) { pstmt = con.prepareStatement(sql2); pstmt.setInt(1,
	 * comment.getZ_cref()); pstmt.setInt(2, comment.getZ_cre_step());
	 * pstmt.executeUpdate(); pstmt.close();
	 * comment.setZ_cre_step(comment.getZ_cre_step() + 1);
	 * comment.setZ_cre_level(comment.getZ_cre_level() + 1); con.commit(); }
	 * 
	 * pstmt = con.prepareStatement(sql1); rs = pstmt.executeQuery(); con.commit();
	 * if (rs.next()) { number = rs.getInt(1); con.commit(); } rs.close();
	 * pstmt.close();
	 * 
	 * if (z_cnumber==0) { comment.setZ_cref(number); con.commit(); }
	 * 
	 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, number); pstmt.setString(2,
	 * comment.getMem_id()); pstmt.setInt(3, comment.getZ_number());
	 * pstmt.setString(4, comment.getZ_cbody()); pstmt.setString(5,
	 * comment.getZ_cnickname()); pstmt.setTimestamp(6, comment.getZ_cdate());
	 * pstmt.setInt(7, comment.getZ_cref()); pstmt.setInt(8,
	 * comment.getZ_cre_step()); pstmt.setInt(9, comment.getZ_cre_level());
	 * result=pstmt.executeUpdate(); con.commit();
	 * System.out.println("��۾��� ��������(result)=>"+result); } catch (Exception e) {
	 * System.out.println("insertComment() ���� �߻�=>"+e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * }
	 */

	// �Խ����� �۾��� �� �亯�� ����

	public void insertComment(ZCommentDTO comment) {
		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int z_cnumber = comment.getZ_cnumber();
		int z_cref = comment.getZ_cref();
		int z_cre_step = comment.getZ_cre_step();
		int z_cre_level = comment.getZ_cre_level();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertComment()�޼��� ���� z_cnumber=>" + z_cnumber);
		System.out.println("z_cref=>" + z_cref + ", z_cre_step=>" + z_cre_step + ", z_cre_level=>" + z_cre_level);
		
		int point, grade;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(z_cnumber) from z_comment";
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
			if (z_cnumber != 0) {
				// ���� �׷��ȣ�� ������ �����鼭(ref=?) ������ step���� ū �Խù�(re_step>?)�� ã�Ƽ� �� step �ϳ� ����
				// @@----->�������ѳ��� �ڱⰡ 1�� �ż� ������ ����

				System.out.println("update ���� z_cre_step=>" + z_cre_step);

				// z_cref�� ���� ��۵� �� step���� ���� ���� �� ã�Ƽ� �װͺ��� 1 ũ�� ����� ����
				String sql2 = "select max(z_cre_step) from z_comment where z_cref=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setInt(1, z_cref);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					z_cre_step = rs.getInt(1) + 1;
					System.out.println("z_cre_step�� �ִ�+1=>" + z_cre_step);
				} else {
					z_cre_step = z_cre_step + 1;
				}
				z_cre_level = z_cre_level + 1;
				System.out.println("update ���� z_cre_step=>" + z_cre_step + ", z_cre_level=>" + z_cre_level);
				con.commit();

				/*
				 * sql="update z_comment set z_cre_step=z_cre_step+1 where z_cref=? and z_cre_step>?"
				 * ;
				 * 
				 * System.out.println("insertComment()�� sql=>"+sql);
				 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, z_cref); pstmt.setInt(2,
				 * z_cre_step); int update=pstmt.executeUpdate(); con.commit();
				 * System.out.println("��ۼ�������(update)=>"+update+" //(1���� 0����)"); //1���� 0����
				 * System.out.println("update �ϰ� ������ z_cre_step()=>"+z_cre_step);
				 */
				// �亯��
				// z_cre_step=z_cre_step + 1;

			} else { // �űԱ��̶�� num=0
				z_cref = number;
				z_cre_step = 0;
				z_cre_level = 0;
				con.commit();
			}
			sql = "insert into z_comment(z_cnumber, mem_id, z_number, z_cbody, z_cnickname, z_cdate, ";
			sql += " z_cref, z_cre_step, z_cre_level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, comment.getMem_id());
			pstmt.setInt(3, comment.getZ_number());
			pstmt.setString(4, comment.getZ_cbody());
			pstmt.setString(5, comment.getZ_cnickname());
			pstmt.setTimestamp(6, comment.getZ_cdate());

			pstmt.setInt(7, z_cref);
			pstmt.setInt(8, z_cre_step);
			pstmt.setInt(9, z_cre_level);

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
	public int deleteComment(int z_cnumber) {

		int x = -1;

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from z_comment where z_cnumber=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, z_cnumber);

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
	public int upDeleteComment(int z_cnumber, int z_cref, int z_cre_level) {

		int x = -1; // �Խù� �������� 1���� 0����

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			String sql1="select * from z_comment where z_cref=? and z_cre_level>?";
			pstmt=con.prepareStatement(sql1);
			pstmt.setInt(1, z_cref);
			pstmt.setInt(2, z_cre_level);
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //���۵��� �ִ� ����̶��
				sql = "update z_comment set z_cnickname=' ', z_cbody='������ ����Դϴ�.&nbsp;' where z_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, z_cnumber);
			}else {
				sql = "delete from z_comment where z_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, z_cnumber);
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
	public int getCommentCount(int z_number) { // getmemberCount()->MemberDAO���� �ۼ�
		int x = 0;
		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from z_comment where z_number=? and z_cbody!='������ ����Դϴ�.&nbsp;'";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, z_number);
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
