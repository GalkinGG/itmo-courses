import requests
from bs4 import BeautifulSoup
import pandas as pd
from sklearn.preprocessing import MinMaxScaler
import re
import arff

ATTRIBUTES = {
    'Бренд': 'STRING',
    'Цена': 'NUMERIC',
    'Старая цена': 'NUMERIC',
    'Цвет': 'STRING',
    'Тип': 'STRING',
    'Сезон': 'STRING',
    'Коллекция': 'STRING',
    'Материал верха основной': 'STRING',
    'Материал подкладки основной': 'STRING',
    'Материал подкладки дополнительный': 'STRING',
    'Материал стельки': 'STRING',
    'Материал подошвы': 'STRING',
    'Перфорация': 'STRING',
    'Тип застежки': 'STRING',
    'Высота каблука (мм)': 'NUMERIC',
    'Высота верха обуви (мм)': 'NUMERIC',
    'Полнота': 'NUMERIC',
    'Пол': 'STRING',
}

ATTRIBUTES_TRANSLATION = {
    'Бренд': 'Brand',
    'Цена': 'Price',
    'Старая цена': 'Old price',
    'Цвет': 'Color',
    'Тип': 'Type',
    'Сезон': 'Season',
    'Коллекция': 'Collection',
    'Материал верха основной': 'Upper Material',
    'Материал подкладки основной': 'Lining Material',
    'Материал подкладки дополнительный': 'Additional Lining Material',
    'Материал стельки': 'Insole Material',
    'Материал подошвы': 'Sole Material',
    'Перфорация': 'Perforation',
    'Тип застежки': 'Closure Type',
    'Высота каблука (мм)': 'Heel Height (mm)',
    'Высота верха обуви (мм)': 'Upper Height (mm)',
    'Полнота': 'Width',
    'Пол': 'Gender',
}

possible_values = {
    'Бренд': set(),
    'Цвет': set(),
    'Тип': set(),
    'Сезон': set(),
    'Коллекция': set(),
    'Материал верха основной': set(),
    'Материал подкладки основной': set(),
    'Материал подкладки дополнительный': set(),
    'Материал стельки': set(),
    'Материал подошвы': set(),
    'Перфорация': set(),
    'Тип застежки': set(),
    'Пол': set(),
}


# Функция для получения HTML кода страницы
def get_html(url):
    headers = {'User-Agent': 'Mozilla/5.0'}
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        return response.text
    else:
        print(f"Failed to retrieve page, status code: {response.status_code}")
    return None


# Функция для получения характеристик товара
def get_product_details(product_url):
    html = get_html(product_url)
    if not html:
        return {}

    soup = BeautifulSoup(html, 'html.parser')
    details = {}

    try:
        price_text = soup.find('span', class_='product-price__current highlight').text.strip()
        price = int(re.sub(r'\D', '', price_text))  # Очистка от символов
    except AttributeError:
        price = None

    try:
        old_price_text = soup.find('del', class_='product-price__old').text.strip()
        old_price = int(re.sub(r'\D', '', old_price_text))
    except AttributeError:
        old_price = price  # Если нет старой цены, используем текущую

    details["Цена"] = price
    details["Старая цена"] = old_price

    # Извлечение характеристик
    about_cols = soup.find_all('dl', class_='product-article__about-col')

    for dl in about_cols:
        dt_elements = dl.find_all('dt')
        dd_elements = dl.find_all('dd')

        for dt, dd in zip(dt_elements, dd_elements):
            key = dt.get_text(strip=True)
            value = dd.get_text(strip=True) if dd.get_text(strip=True) else dd.find('span').get_text(strip=True)

            details[key] = value

            if key in possible_values:
                possible_values[key].add(value)

    return details


# Функция парсинга страницы и сбора данных
def parse_sneakers_page(html):
    soup = BeautifulSoup(html, 'html.parser')
    products_list = soup.find('div', class_='products-list__main js-list-view')
    sneakers = products_list.find_all('div', class_='product-card products-list__item js-reveal swiper-container')
    data = []

    for sneaker in sneakers:
        # Получение ссылки на товар
        product_link = sneaker.find('a', class_='product-card__image')['href']
        product_url = f"https://thomas-muenz.ru{product_link}"  # Формирование полного URL

        # Получение характеристик товара
        product_details = get_product_details(product_url)

        data.append({i: product_details.get(i, 'Unknown') for i in ATTRIBUTES.keys()})

    return data


# Функция для парсинга всех страниц каталога
def parse_all_pages(base_url, num_pages):
    all_data = []

    for page in range(1, num_pages + 1):
        url = f"{base_url}?nav=page-{page}"
        html = get_html(url)
        if html:
            sneakers_data = parse_sneakers_page(html)
            all_data.extend(sneakers_data)
        else:
            print(f"Failed to retrieve page {page}")

    return all_data

#Сохранение данных в tsv и arff форматах
def write_raw_data(df):
    df.to_csv('sneakers_data.tsv', sep='\t', index=False)

    arff_data = {
        'description': 'Sneakers dataset',
        'relation': 'sneakers',
        'attributes': [
            (ATTRIBUTES_TRANSLATION[key], [*possible_values[key]] if key in possible_values else ATTRIBUTES[key]) for
            key in
            ATTRIBUTES.keys()],
        'data': df.values.tolist(),
    }
    with open('sneakers_data.arff', 'w', encoding='utf-8') as f:
        arff.dump(arff_data, f)

    print("Данные сохранены в .tsv и .arff форматах")

#Сохранение преобразованных данных в формате csv
def write_to_csv():
    df = pd.read_csv('sneakers_data.tsv', sep='\t')

    one_value_categories = [key for key in possible_values.keys() if len(possible_values[key]) == 1]
    df.drop(one_value_categories, axis=1, inplace=True)

    # Замена 'Unknown' на моду для каждого столбца
    for column in df.columns:
        if df[column].dtype == object:  # Проверка на строковые столбцы
            most_common_value = df[column].mode()[0]
            df[column] = df[column].replace('Unknown', most_common_value)

    #One-Hot Encoding для категориальных признаков
    categorical_columns = [key for key, value in ATTRIBUTES.items() if
                           value == 'STRING' and key != 'Бренд' and key not in one_value_categories]
    df = pd.get_dummies(df, columns=categorical_columns, dummy_na=False, dtype=int)

    numeric_columns = [key for key, value in ATTRIBUTES.items() if value == 'NUMERIC']
    scaler = MinMaxScaler()
    df[numeric_columns] = scaler.fit_transform(df[numeric_columns]).round(8)

    df.to_csv('sneakers_data.csv', sep=',', index=False)

    print("Данные обработаны и сохранены в 'sneakers_data_processed.csv'.")


def main():
    base_url = 'https://thomas-muenz.ru/catalog/men/shoes/sneakers/'
    all_sneakers = parse_all_pages(base_url, 29)
    df = pd.DataFrame(all_sneakers)
    write_raw_data(df)
    write_to_csv()

    print("Парсинг завершён, данные сохранены!")


if __name__ == "__main__":
    main()
