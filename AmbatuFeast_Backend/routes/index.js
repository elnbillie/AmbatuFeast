var API_KEY = "1234";

var express = require('express')
var router = express.Router();
const { pool } = require('../db');
var crypto = require('crypto');
var uuid = require('uuid');
var bodyParser = require('body-parser');

//password ultil
var genRandomString = function (length) {
    return crypto.randomBytes(Math.ceil(length / 2))
        .toString('hex')
        .slice(0, length);
}

var sha512 = function (password, salt) {
    var hash = crypto.createHmac('sha512', salt);
    hash.update(password);
    var value = hash.digest('hex');
    return {
        salt: salt,
        passwordHash: value
    };
}

function saltHashPassword(userPassword) {
    var salt = genRandomString(16);
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

function checkHashPassword(userPassword,salt){
    var passwordData = sha512(userPassword,salt);
    return passwordData;
}

router.post('/register', (req, res, next) => {
    var post_data = req.body;
    var uid = uuid.v4();
    var plaint_password = post_data.password;
    var hash_data = saltHashPassword(plaint_password);
    var password = hash_data.passwordHash;
    var salt = hash_data.salt;

    var name = post_data.name;
    var email = post_data.email;

    pool.query('SELECT * FROM pengguna WHERE email = $1', [email], function (err, result) {
        if (err) {
            console.log('[POSTGRES ERROR]', err);
            return res.json('Register error: ', err);
        }

        if (result.rows.length)
            return res.json('User already exists!');

        pool.query('INSERT INTO pengguna (unique_id, name, email, encrypted_password, salt, created_at, updated_at) VALUES ($1, $2, $3, $4, $5, NOW(), NOW())', 
        [uid, name, email, password, salt], function (err, result) {
            if (err) {
                console.log('[POSTGRES ERROR]', err);
                return res.json('Register error: ', err);
            }
            return res.json('Register Successful');
        });
    });
});

router.post('/login', (req, res, next) => {
    var post_data = req.body;

    // Extract email and password from request
    var user_password = post_data.password;
    var email = post_data.email;

    pool.query('SELECT * FROM pengguna WHERE email = $1', [email], function (err, result) {
        if (err) {
            console.log('[POSTGRES ERROR]', err);
            return res.status(500).json({ message: 'Error fetching user: ' + err.message });
        }

        if (result.rows.length) {
            var salt = result.rows[0].salt;
            var encrypted_password = result.rows[0].encrypted_password;
            var hashed_password = checkHashPassword(user_password, salt).passwordHash;
            if (encrypted_password == hashed_password) {
                res.json(result.rows[0]);
            } else {
                res.json('Wrong Password');
            }
        } else {
            res.json('User not exists');
        }
    });
});



/*router.get("/",(req,res,next)=>{
    console.log('Password: 123456');
    var encrypt = saltHashPassword("123456");
    console.log('Encrypt: ' + saltHashPassword("123456").passwordHash);
    console.log('Salt: ' + encrypt.salt);
})*/


router.get('/', function (req, res) {
    res.send("API RUNNING");
});

router.get('/user', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var fbid = req.query.fbid;
        if (fbid != null) {
            try {
                const client = await pool.connect();
                const queryResult = await client.query('SELECT userPhone, name, address, fbid FROM "User" WHERE fbid = $1', [fbid]); // using parameterized query
                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        } else {
            res.send(JSON.stringify({ success: false, message: "Missing fbid" }));
        }
    }
});

router.post('/user', async (req, res, next) => {
    console.log(req.body);
    if (req.body.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API key" }));
    } else {
        var user_phone = req.body.userPhone;
        var user_name = req.body.userName;
        var user_address = req.body.userAddress;
        var fbid = req.body.fbid;

        if (fbid != null) {
            try {
                const client = await pool.connect();
                const queryResult = await client.query(`
                    WITH upsert AS (
                        UPDATE "User" SET "userphone" = $2, "name" = $3, "address" = $4 WHERE "fbid" = $1 RETURNING *
                    )
                    INSERT INTO "User" ("fbid", "userphone", "name", "address")
                    SELECT $1, $2, $3, $4 WHERE NOT EXISTS (SELECT * FROM upsert)
                `, [fbid, user_phone, user_name, user_address]);
                client.release();

                if (queryResult.rowCount > 0) {
                    res.send(JSON.stringify({ success: true, message: "Success" }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Operation failed" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        } else {
            res.send(JSON.stringify({ success: false, message: "Missing fbid in body of POST request" }));
        }
    }
});

//===============================================
//RESTAURANT TABLE
//GET
//===============================================

router.get('/restaurant', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        try {
            const client = await pool.connect();

            const queryResult = await client.query('SELECT id, name, address, phone, lat, lng, userOwner, image, paymentUrl FROM "restaurant"');
            client.release();

            if (queryResult.rows.length > 0) {
                res.send(JSON.stringify({ success: true, result: queryResult.rows }));
            } else {
                res.send(JSON.stringify({ success: false, message: "Empty" }));
            }
        } catch (err) {
            res.status(500); // internal server error
            res.send(JSON.stringify({ success: false, message: err.message }));
        }
    }
});

router.get('/restaurantById', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key !== API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        const restaurantId = req.query.restaurantId;
        if (restaurantId) {
            try {
                const client = await pool.connect();
                const queryResult = await client.query({
                    text: 'SELECT id, name, address, phone, lat, lng, userOwner, image, paymentUrl FROM restaurant WHERE id = $1',
                    values: [restaurantId]
                });
                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        } else {
            res.send(JSON.stringify({ success: false, message: "Missing restaurantId in query" }));
        }
    }
});

router.get('/nearbyrestaurant', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key !== API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var user_lat = parseFloat(req.query.lat);
        var user_lng = parseFloat(req.query.lng);
        var distance = parseInt(req.query.distance);

        if (!isNaN(user_lat) && !isNaN(user_lng)) {
            try {
                const client = await pool.connect();
                const queryResult = await client.query(
                    'SELECT id, name, address, phone, lat, lng, userOwner, image, paymentUrl, ' +
                    'TRUNC((111.045 * DEGREES(ACOS(COS(RADIANS($1)) * COS(RADIANS(lat)) ' +
                    '* COS(RADIANS(lng) - RADIANS($2)) + SIN(RADIANS($1)) ' +
                    '* SIN(RADIANS(lat)))) * 100) / 100) AS distance_in_km ' +
                    'FROM restaurant ' +
                    'WHERE (111.045 * DEGREES(ACOS(COS(RADIANS($1)) * COS(RADIANS(lat)) ' +
                    '* COS(RADIANS(lng) - RADIANS($2)) + SIN(RADIANS($1)) ' +
                    '* SIN(RADIANS(lat)))) * 100) / 100 < $3',
                    [user_lat, user_lng, distance]
                );

                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "No restaurants found within the specified distance" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        } else {
            res.send(JSON.stringify({ success: false, message: "Invalid latitude or longitude" }));
        }
    }
});

//===============================================
//MENU TABLE
//GET
//===============================================

router.get('/menu', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var restaurant_id = req.query.restaurantId;
        if (restaurant_id != null) {
            try {
                const client = await pool.connect();

                const queryResult = await client.query(
                    'SELECT id, name, description, image FROM "menu" WHERE id IN ' +
                    '(SELECT menuId FROM "restaurant_menu" WHERE restaurantId = $1)',
                    [restaurant_id]
                );
                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500); // internal server error
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing restaurantId in query" }));
        }
    }
});

//===============================================
//FOOD TABLE
//GET
//===============================================

router.get('/food', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var menu_id = req.query.menuId;
        if (menu_id != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT id, name, description, image, price,isSize, isAddon, discount FROM food WHERE id IN ' +
                    '(SELECT foodId FROM menu_food WHERE menuId = $1)',
                    [menu_id]
                );
                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500); // internal server error
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing menu_id in query" }));
        }
    }
});

router.get('/foodById', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var food_id = req.query.foodId;
        if (food_id != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT id, name, description, image, price, isSize, isAddon, discount FROM food WHERE id = $1',
                    [food_id]
                );

                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500); // internal server error
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing menu_id in query" }));
        }
    }
});

router.get('/searchFood', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var search_query = req.query.foodName;
        if (search_query != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT id, name, description, image, price, isSize, isAddon, discount FROM food WHERE LOWER(name) LIKE LOWER($1)',
                    ['%' + search_query + '%']
                );


                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing foodName in query" }));
        }
    }
});

//===============================================
//SIZE TABLE
//GET
//===============================================

router.get('/size', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var food_id = req.query.foodId;
        if (food_id != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT id, description, extraPrice FROM size WHERE id IN ' +
                    '(SELECT sizeId FROM food_size WHERE foodId = $1)',
                    [food_id]
                );

                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500); // internal server error
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing menu_id in query" }));
        }
    }
});

//===============================================
//ADDON TABLE
//GET
//===============================================

router.get('/addon', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var food_id = req.query.foodId;
        if (food_id != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT id, description, extraPrice FROM addon WHERE id IN ' +
                    '(SELECT addonId FROM food_addon WHERE foodId = $1)',
                    [food_id]
                );

                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500); // internal server error
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing menu_id in query" }));
        }
    }
});

//===============================================
//ORDER AND ORDER DETAIL TABLE
//GET/POST
//===============================================

router.get('/order', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var order_fbid = req.query.orderFBID;
        if (order_fbid != null) {
            try {
                const client = await pool.connect();

                const queryResult = await pool.query(
                    'SELECT orderId, orderFBID, orderPhone, orderName, orderAddress, orderStatus, ' +
                    'orderDate, restaurantId, transactionId, cod, totalPrice, numOfItem ' +
                    'FROM "Order" WHERE orderFBID = $1',
                    [order_fbid]
                );


                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing orderFBID in query" }));
        }
    }
});

router.get('/orderDetail', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API Key" }));
    } else {
        var order_id = req.query.orderId;
        if (order_id != null) {
            try {
                const client = await pool.connect();

                const queryText = 'SELECT orderId, itemId, quantity, discount, extraPrice, size, addOn FROM "OrderDetail" WHERE orderId = $1';
                const queryResult = await pool.query(queryText, [order_id]);


                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        }
        else {
            res.send(JSON.stringify({ success: false, message: "Missing orderId in query" }));
        }
    }
});


router.post('/createOrder', async (req, res, next) => {
    console.log(req.body);
    if (req.body.key !== API_KEY) {
        res.send(JSON.stringify({ success: false, message: "Wrong API key" }));
    } else {
        const order_phone = req.body.orderPhone;
        const order_name = req.body.orderName;
        const order_address = req.body.orderAddress;
        const order_date = req.body.orderDate;
        const restaurant_id = req.body.restaurantId;
        const transaction_id = req.body.transactionId;
        const cod = req.body.cod;
        const total_price = req.body.totalPrice;
        const num_of_item = req.body.numOfItem;
        const order_fbid = req.body.orderFBID;

        if (order_fbid != null) {
            try {
                const client = await pool.connect();
                const queryText = `
                    INSERT INTO "Order" 
                    (OrderFBID, OrderPhone, OrderName, OrderAddress, OrderStatus, OrderDate, RestaurantId, TransactionId, COD, TotalPrice, NumOfItem)
                    VALUES 
                    ($1, $2, $3, $4, 0, $5, $6, $7, $8, $9, $10)
                    RETURNING OrderId AS orderNumber;`;

                const queryResult = await client.query(queryText, [
                    order_fbid,
                    order_phone,
                    order_name,
                    order_address,
                    order_date,
                    restaurant_id,
                    transaction_id,
                    cod ? 1 : 0,
                    total_price,
                    num_of_item
                ]);

                client.release();

                if (queryResult.rows.length > 0) {
                    res.send(JSON.stringify({ success: true, result: queryResult.rows }));
                } else {
                    res.send(JSON.stringify({ success: false, message: "Empty" }));
                }
            } catch (err) {
                res.status(500);
                res.send(JSON.stringify({ success: false, message: err.message }));
            }
        } else {
            res.send(JSON.stringify({ success: false, message: "Missing orderFBID in body of POST request" }));
        }
    }
});

router.post('/updateOrder', async (req, res, next) => {
    console.log(req.body);
    if (req.body.key !== API_KEY) {
        return res.send(JSON.stringify({ success: false, message: "Wrong API key" }));
    }

    const { orderId, OrderDetail } = req.body;

    if (!orderId || !OrderDetail) {
        return res.send(JSON.stringify({ success: false, message: "Missing orderId or orderDetail in body of POST request" }));
    }

    const client = await pool.connect();
    try {
        await client.query('BEGIN');

        const queryText = `
            INSERT INTO "OrderDetail" 
            (orderid, itemid, Quantity, Price, Discount, Size,Addon, ExtraPrice)
            VALUES 
            ($1, $2, $3, $4, $5, $6, $7, $8);
        `;

        for (const detail of OrderDetail) {
            await client.query(queryText, [
                orderId,
                detail.foodId,
                detail.foodQuantity,
                detail.foodPrice,
                detail.foodDiscount,
                detail.foodSize,
                detail.foodAddon,
                parseFloat(detail.foodExtraPrice)
            ]);
        }

        await client.query('COMMIT');
        res.send(JSON.stringify({ success: true, message: "Update successful" }));
    } catch (err) {
        await client.query('ROLLBACK');
        console.error("Error during transaction", err);
        res.status(500).send(JSON.stringify({ success: false, message: err.message }));
    } finally {
        client.release();
    }
});

//===============================================
//FAVORITE
//GET/POST/DELETE
//===============================================

router.get('/favorite', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        return res.status(401).json({ success: false, message: "Wrong API Key" });
    }

    var email = req.query.email;
    if (!email) {
        return res.status(400).json({ success: false, message: "Missing email in query" });
    }

    try {
        const client = await pool.connect();
        console.log("Database connected successfully");

        const queryText = 'SELECT email, foodid, restaurantid, restaurantname, foodname, foodimage, price FROM "Favorite" WHERE email = $1;';
        const queryResult = await pool.query(queryText, [email]);

        client.release();

        if (queryResult.rows.length > 0) {
            console.log("Query result:", queryResult.rows);
            return res.status(200).json({ success: true, result: queryResult.rows });
        } else {
            return res.status(404).json({ success: false, message: "Empty" });
        }
    } catch (err) {
        console.error("Error executing query", err);
        return res.status(500).json({ success: false, message: err.message });
    }
});


router.get('/favoriteByRestaurant', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key != API_KEY) {
        return res.status(401).json({ success: false, message: "Wrong API Key" });
    }

    var email = req.query.email;
    var restaurant_id = req.query.restaurantId;
    if (!email || !restaurant_id) {
        return res.status(400).json({ success: false, message: "Missing email or restaurantId in query" });
    }

    try {
        const client = await pool.connect();
        console.log("Database connected successfully");

        const queryText = 'SELECT email, foodid, restaurantid, restaurantname, foodname, foodimage, price FROM "Favorite" WHERE email = $1 AND restaurantid = $2;';
        const queryResult = await pool.query(queryText, [email, restaurant_id]);

        client.release();

        if (queryResult.rows.length > 0) {
            return res.status(200).json({ success: true, result: queryResult.rows });
        } else {
            return res.status(404).json({ success: false, message: "Empty" });
        }
    } catch (err) {
        console.error("Error executing query", err);
        return res.status(500).json({ success: false, message: err.message });
    }
});


router.post('/favorite', async (req, res, next) => {
    console.log(req.body);
    if (req.body.key !== API_KEY) {
        return res.status(401).json({ success: false, message: "Wrong API key" });
    }

    const {
        email, foodId, restaurantId, restaurantName, foodName, foodImage, price
    } = req.body;

    if (!email) {
        return res.status(400).json({ success: false, message: "Missing email in body of POST request" });
    }

    try {
        const client = await pool.connect();

        const queryText = `
            INSERT INTO "Favorite"
            (email, FoodId, RestaurantId, RestaurantName, FoodName, FoodImage, price)
            VALUES
            ($1, $2, $3, $4, $5, $6, $7)
            ON CONFLICT (email, FoodId, RestaurantId)
            DO NOTHING;
        `;

        console.log("Executing query:", queryText);
        await client.query(queryText, [email, foodId, restaurantId, restaurantName, foodName, foodImage, price]);
        console.log("Query executed successfully");

        client.release();

        return res.status(200).json({ success: true, message: "Favorite added successfully" });
    } catch (err) {
        console.error("Error executing query", err);
        return res.status(500).json({ success: false, message: err.message });
    }
});






router.delete('/favorite', async (req, res, next) => {
    console.log(req.query);
    if (req.query.key !== API_KEY) {
        return res.status(401).json({ success: false, message: "Wrong API key" });
    }

    const { email, foodId, restaurantId } = req.query;

    if (!email || !foodId || !restaurantId) {
        return res.status(400).json({ success: false, message: "Missing email, foodId, or restaurantId in query" });
    }

    try {
        const client = await pool.connect();
        const queryText = 'DELETE FROM "Favorite" WHERE email = $1 AND FoodId = $2 AND RestaurantId = $3;';
        const queryResult = await client.query(queryText, [email, foodId, restaurantId]);

        client.release();

        if (queryResult.rowCount > 0) {
            return res.status(200).json({ success: true, message: "Favorite successfully deleted" });
        } else {
            return res.status(404).json({ success: false, message: "No favorite found with the provided ids" });
        }
    } catch (err) {
        console.error("Error executing query", err);
        return res.status(500).json({ success: false, message: err.message });
    }
});



module.exports = router;
