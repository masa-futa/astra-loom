import SwiftUI

/// スペクトル型から星の物理的特徴を導出
struct StarProperties {
    let temperature: String
    let colorDescription: String
    let starType: String
    let color: Color

    init(spectralType: String?) {
        guard let type = spectralType?.uppercased().first else {
            self.temperature = "不明"
            self.colorDescription = "白色"
            self.starType = "不明"
            self.color = .white
            return
        }

        // スペクトル型による分類
        switch type {
        case "O":
            self.temperature = "30,000 - 50,000 K"
            self.colorDescription = "青白色"
            self.starType = "高温の主系列星"
            self.color = Color(red: 155/255, green: 176/255, blue: 255/255)

        case "B":
            self.temperature = "10,000 - 30,000 K"
            self.colorDescription = "青白色"
            self.starType = "高温の主系列星"
            self.color = Color(red: 170/255, green: 191/255, blue: 255/255)

        case "A":
            self.temperature = "7,500 - 10,000 K"
            self.colorDescription = "白色"
            self.starType = "主系列星"
            self.color = Color(red: 202/255, green: 215/255, blue: 255/255)

        case "F":
            self.temperature = "6,000 - 7,500 K"
            self.colorDescription = "黄白色"
            self.starType = "主系列星"
            self.color = Color(red: 248/255, green: 247/255, blue: 255/255)

        case "G":
            self.temperature = "5,200 - 6,000 K"
            self.colorDescription = "黄色"
            self.starType = "主系列星（太陽と同じタイプ）"
            self.color = Color(red: 255/255, green: 244/255, blue: 234/255)

        case "K":
            self.temperature = "3,700 - 5,200 K"
            self.colorDescription = "橙色"
            self.starType = "低温の主系列星または巨星"
            self.color = Color(red: 255/255, green: 210/255, blue: 161/255)

        case "M":
            self.temperature = "2,400 - 3,700 K"
            self.colorDescription = "赤色"
            self.starType = "低温の主系列星または巨星"
            self.color = Color(red: 255/255, green: 204/255, blue: 111/255)

        default:
            self.temperature = "不明"
            self.colorDescription = "白色"
            self.starType = "不明"
            self.color = .white
        }
    }

    /// 光度階級から星のタイプを詳細化
    static func refineStarType(spectralType: String) -> String {
        let upper = spectralType.uppercased()

        // 光度階級を確認
        if upper.contains("I") && !upper.contains("V") {
            if upper.contains("IA") {
                return "超巨星"
            } else if upper.contains("IB") {
                return "輝巨星"
            } else {
                return "超巨星"
            }
        } else if upper.contains("II") {
            return "輝巨星"
        } else if upper.contains("III") {
            return "巨星"
        } else if upper.contains("IV") {
            return "準巨星"
        } else if upper.contains("V") {
            return "主系列星"
        }

        // デフォルト
        return StarProperties(spectralType: spectralType).starType
    }
}
