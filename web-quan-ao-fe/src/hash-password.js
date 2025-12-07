const mssql = require('mssql');
const bcrypt = require('bcrypt');

const config = {
  user: 'sa',
  password: '123',
  server: '127.0.0.1',
  database: 'sportystore',
  options: { encrypt:false, trustServerCertificate:true }
};

(async () => {
  const pool = await mssql.connect(config);
  try {
    const res = await pool.request().query("SELECT id, password FROM user_account");
    for(const row of res.recordset){
      const id = row.id;
      const pwd = row.password || '';
      if(pwd.startsWith('$2a$') || pwd.startsWith('$2b$') || pwd.startsWith('$2y$')) {
        console.log(`skip hashed id=${id}`);
        continue;
      }
      const hash = await bcrypt.hash(pwd, 10);
      await pool.request()
        .input('hash', mssql.NVarChar(255), hash)
        .input('id', mssql.BigInt, id)
        .query('UPDATE user_account SET password = @hash WHERE id = @id');
      console.log(`hashed id=${id}`);
    }
    console.log('done');
  } finally {
    pool.close();
  }
})();
